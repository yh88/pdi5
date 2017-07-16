/*******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2012 by Pentaho : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.pentaho.di.trans.steps.googleanalyticsplus;

import java.io.File;
import java.io.IOException;

import java.util.List;

import org.apache.derby.tools.sysinfo;
import org.pentaho.di.core.Const;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.*;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.AnalyticsScopes;
import com.google.api.services.analytics.Analytics.Data.Ga.Get;
import com.google.api.services.analytics.model.GaData;


public class GaInputPlusStep extends BaseStep implements StepInterface {

	private GaInputPlusStepData data;
	private GaInputPlusStepMeta meta;
	
	//private static Class<?> PKG = GaInputStep.class; // for i18n purposes
	
	public GaInputPlusStep(StepMeta s, StepDataInterface stepDataInterface, int c, TransMeta t, Trans dis) {
		super(s, stepDataInterface, c, t, dis);
	}

	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {

		meta = (GaInputPlusStepMeta) smi;
		data = (GaInputPlusStepData) sdi;

		if (first) {
			
			first = false;
			
			data.outputRowMeta = new RowMeta();
			meta.getFields(data.outputRowMeta, getStepname(), null, null, this);
			
            // stores the indices where to look for the key fields in the input rows
            data.conversionMeta = new ValueMetaInterface[meta.getFeedField().length];

            
            for (int i=0;i<meta.getFeedField().length;i++){
            	
            	// get output and from-string conversion format for each field
                ValueMetaInterface returnMeta = data.outputRowMeta.getValueMeta(i);
                
                ValueMetaInterface conversionMeta = returnMeta.clone();
                if (meta.getFeedFieldType()[i].equals(GaInputPlusStepMeta.FIELD_TYPE_CONFIDENCE_INTERVAL)){
                	conversionMeta.setType(ValueMetaInterface.TYPE_NUMBER);
                }
                else{
                	conversionMeta.setType(ValueMetaInterface.TYPE_STRING);	
                }
                
                conversionMeta.setConversionMask(meta.getConversionMask()[i]);
                
    			conversionMeta.setDecimalSymbol("."); // google analytics is en-US
    			conversionMeta.setGroupingSymbol(null); // google analytics uses no grouping symbol

                data.conversionMeta[i] = conversionMeta;
                
            }   

		}
		
		// generate output row, make it correct size
		Object[] outputRow = RowDataUtil.allocateRowData(data.outputRowMeta.size());
		

			data.gaData = getNextGaDataEntry();

		if (data.gaData !=null  && (meta.getRowLimit() <= 0 || getLinesWritten() < meta.getRowLimit())){ // another record to process

		      for (List<String> rowValues : data.gaData.getRows()) {
		    	  for (int j = 0; j < rowValues.size(); j++) {
		    		  
			        	String value = null;
			        	String fieldName = meta.getFeedField()[j];
			        	String fieldType = meta.getFeedFieldType()[j];
			        	// confidence intervals
			        	if (fieldType.equals(GaInputPlusStepMeta.FIELD_TYPE_DIMENSION)){

			            	value = rowValues.get(j);
			            	outputRow[j] = data.outputRowMeta.getValueMeta(j).convertData(data.conversionMeta[j], value);
			        	}
			        	else if (fieldType.equals(GaInputPlusStepMeta.FIELD_TYPE_METRIC)){
			        		value = rowValues.get(j);

			            	outputRow[j] = data.outputRowMeta.getValueMeta(j).convertData(data.conversionMeta[j], value);
			        	}
			        	
		    		  
//	    				  outputRow[j] = data.outputRowMeta.getValueMeta(j).convertData(data.conversionMeta[j],rowValues.get(j));
				}
		    	  
		    	  
//		    	  putRow(data.outputRowMeta, outputRow); 
 try {
	 				
					putRow(data.outputRowMeta, outputRow); 
					Thread.sleep(13);
					
						
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

		      }
//		      data.gaData = null; //测试数据流专用

			// Some basic logging
			if (checkFeedback(getLinesWritten())) {
				if (log.isBasic()) logBasic("Linenr " + getLinesWritten()); 
			}
			return true;			
		}
		else{
			setOutputDone();
			return false;
		}
		
		
	}	
	
	/*
	 * 新增 返回GetGadataQuery方法
	 */
	
	
protected Get getGaDataQuery(){
		
		Get get = null;

		String profileId = "ga:" + (meta.isUseCustomTableId()?environmentSubstitute(meta.getGaCustomTableId()):meta.getGaProfileTableId());
		String start_data = environmentSubstitute(meta.getStartDate());
		String end_date = environmentSubstitute(meta.getEndDate());
		String metric = environmentSubstitute(meta.getMetrics());
		String dimensions = environmentSubstitute(meta.getDimensions());
		
		//账户验证
		try {
			data.analytics = initializeAnalytics();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println("滚蛋~验证不通过......");
			e.printStackTrace();
		}
		
		System.out.println("come in ....");
		try {
			data.analytics = initializeAnalytics();
			System.out.println("Validated ok...");			
			data.entryIndex = 0;
			
			get = data.analytics.data().ga()
	  		        .get( profileId, start_data, end_date, metric)
	  		        .setDimensions(dimensions);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		

		if (meta.isUseCustomSegment()) {
			get.setSegment(environmentSubstitute(meta.getCustomSegment()));
		} else {
			get.setSegment(meta.getSegmentId());
		}

		if (!Const.isEmpty(meta.getFilters())) {
			get.setFilters(environmentSubstitute(meta.getFilters()));
		}
		if (!Const.isEmpty(meta.getSort())) {
			get.setSort(environmentSubstitute(meta.getSort()));
		}
		
		if (!Const.isEmpty(meta.getGaApiKey())){
			get.setKey(environmentSubstitute(meta.getGaApiKey()));
		}

		return get;
		
	}
	

private GaData getNextGaDataEntry() throws KettleException {
	GaData gaData = null;
	
	// no query prepared yet?
	if(data.get == null)
	{
		data.get = getGaDataQuery();
		
		try {
			gaData = data.get.execute();	
			//记录日志 返回查询集有未抽样数据
				logDetailed("Contains Sampled Data: "+gaData.getContainsSampledData());
			System.out.println("Contains Sampled Data: " + gaData.getContainsSampledData());
		} catch (Exception e) {
			throw new KettleException(e);
			// TODO: handle exception
		}
		data.maxRaw = gaData.getTotalResults();
		data.loopIndex = gaData.getRows().size();
		return gaData;
	}
	else if(data.loopIndex < data.maxRaw ){
		data.get.setStartIndex(data.loopIndex+1).setMaxResults(10000);
				
		try {
			System.out.println("当前索引"+data.loopIndex);
			System.out.println("查询最大索引值" + data.maxRaw);
			gaData = data.get.execute();
			data.loopIndex+=gaData.getRows().size();
			System.out.println("增加索引后计数" +data.loopIndex);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new KettleException(e);
		}
		return gaData;
		
	}
	else {
		return null;
	}

}
	
	//新增验证方法

protected  Analytics initializeAnalytics() throws Exception {
    // Initializes an authorized analytics service object.
	 String serviceAccountEmail = environmentSubstitute(meta.getGaSAEmail());
	 String keyFileLocation = environmentSubstitute(meta.getGaApiKey());
	 String appName = environmentSubstitute(meta.getGaAppName());
	 
	  JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	 

    HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    GoogleCredential credential = new GoogleCredential.Builder()
        .setTransport(httpTransport)
        .setJsonFactory(GsonFactory.getDefaultInstance())
        .setServiceAccountId(serviceAccountEmail)
//        .setServiceAccountPrivateKeyFromP12File(new File("F:\\GA\\google api connect-255fbf97dece.p12"))
        .setServiceAccountPrivateKeyFromP12File(new File(keyFileLocation))
        .setServiceAccountScopes(AnalyticsScopes.all())
        .build();

    // Construct the Analytics service object.
    return new Analytics.Builder(httpTransport, JSON_FACTORY, credential)
        .setApplicationName(appName).build();
  }


	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
		meta = (GaInputPlusStepMeta) smi;
		data = (GaInputPlusStepData) sdi;
						       
		return super.init(smi, sdi);
	}

	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
		meta = (GaInputPlusStepMeta) smi;
		data = (GaInputPlusStepData) sdi;
		
		super.dispose(smi, sdi);
	}
	

}
