package org.digitalecmt.etarget.dbentities;

/*-
 * #%L
 * eTarget Maven Webapp
 * %%
 * Copyright (C) 2017 - 2021 digital ECMT
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import java.util.logging.Logger;

public class NgsLibExt extends NgsLib {
//	private static final Logger log = Logger.getLogger(NgsLibExt.class.getName());
	String cfdnaInputColourKeyGreen=null;
	String cfdnaInputColourKeyYellow=null;
	String cfdnaInputColourKeyRed=null;
	String cfDNASample=null;
	Float greenFloat=-1f;
	Float redFloat=-1f;
	Float valueFloat=-1f;
	String cfDNAInputColour=null;
	String averageReadDepthColourKeyGreen=null;
	String averageReadDepthColourKeyYellow=null;
	String averageReadDepthColourKeyRed=null;
	Float greenAvFloat = -1f;
	Float redAvFloat = -1f;
	Float valueAvFloat =-1f;
	String averageReadDepthColour=null;
	String geneResult=null;
	String specimenDateFormatted1=null;
	String tumourPanelName=null;
	
	private NgsLibExt(NgsLib nl) {
		super(nl);
	}
	
	public static NgsLibExt getNgsLibExt(NgsLib nl) {
		return new NgsLibExt(nl);
	}
	
	public static List<NgsLibExt> getNgsLibExtList(List<NgsLib> nll){
		List<NgsLibExt> ngsLibs = new ArrayList<NgsLibExt>();
		for(NgsLib nl :nll) {
			ngsLibs.add(NgsLibExt.getNgsLibExt(nl));
		}
		return ngsLibs;
	}
	
	private void setCfdnaInputColourKeys() {
		if(this.getCfdna_input_colour_key()==null || this.getCfdna_input_colour_key().length()==0) {
			this.setCfdna_input_colour_key("0;0;0");
		}
		String[] colourKeyParts = this.getCfdna_input_colour_key().split(";");
        cfdnaInputColourKeyGreen = colourKeyParts[0];
        cfdnaInputColourKeyYellow = colourKeyParts[1];
        cfdnaInputColourKeyRed = colourKeyParts[2];
	}
	
	private void setAverageReadDepthcolourKeys() {
		if(this.getAverage_read_depth_colour_key()==null) {
			this.setAverage_read_depth_colour_key("0;0;0");
		}
		if(this.getAverage_read_depth()==null || this.getAverage_read_depth().equals("n/a"))  {
			this.setAverage_read_depth("0");
		}
		String[] averageReadParts = this.getAverage_read_depth_colour_key().split(";");
        averageReadDepthColourKeyGreen = averageReadParts[0];
        averageReadDepthColourKeyYellow = averageReadParts[1];
        averageReadDepthColourKeyRed = averageReadParts[2];
        averageReadDepthColour = "";
        // Clean up the key and VALUES
        String greenAv = averageReadDepthColourKeyGreen.replace(">", "");
        String redAv = averageReadDepthColourKeyRed.replace("<", "");
        greenAvFloat = (greenAv.length() > 0) ? Float.parseFloat(greenAv): 0.0f;
        redAvFloat = (redAv.length() > 0) ? Float.parseFloat(redAv): 0.0f;
        valueAvFloat = Float.parseFloat(this.getAverage_read_depth());
        
        // Work out where the value is in the range
        if(greenAvFloat != 0 && valueAvFloat > greenAvFloat) {
          averageReadDepthColour = "green";
        } else if(valueAvFloat <= greenAvFloat && valueAvFloat >= redAvFloat) {
          averageReadDepthColour = "yellow";
        } else if(valueAvFloat < redAvFloat) {
          averageReadDepthColour = "red";
        }
        
	}
	
	private void setCfdnaInputValues() {
		if(this.getNgs_library_cfdna_input()==null) return;
		if(cfdnaInputColourKeyGreen==null) setCfdnaInputColourKeys();
		cfDNASample = this.getNgs_library_cfdna_input();
        cfDNASample = cfDNASample.replace("ng", "");
        // Clean up the key and VALUES
        String green = cfdnaInputColourKeyGreen.replace(" ng", "");
        green = green.replace(">", "");
        String red = cfdnaInputColourKeyRed.replace(" ng", "");
        red = red.replace("<", "");
        String value = cfDNASample.replace(" ng", "");
        
        greenFloat = (green.length() > 0) ? Float.parseFloat(green): 0.0f;
        redFloat = (red.length() > 0) ? Float.parseFloat(red): 0.0f;
        valueFloat = (value.length() > 0) ? Float.parseFloat(value): 0.0f;
        
        // Work out where the value is in the range
        if(greenFloat != 0 && valueFloat > greenFloat) {
          cfDNAInputColour = "green";
        } else if(valueFloat <= greenFloat && valueFloat >= redFloat) {
          cfDNAInputColour = "yellow";
        } else if(valueFloat < redFloat) {
          cfDNAInputColour = "red";
        }
	}
	
	public String getCfdnaInputColourKeyGreen() {
		if(cfdnaInputColourKeyGreen==null) {
			setCfdnaInputColourKeys();
		}
		return cfdnaInputColourKeyGreen;
	}
	
	public String getCfdnaInputColourKeyYellow() {
		if(cfdnaInputColourKeyYellow==null) {
			setCfdnaInputColourKeys();
		}
		return cfdnaInputColourKeyYellow;
	}
	
	public String getCfdnaInputColourKeyRed() {
		if(cfdnaInputColourKeyRed==null) {
			setCfdnaInputColourKeys();
		}
		return cfdnaInputColourKeyRed;
	}
	
	public String getCfDNASample() {
		if(this.cfDNASample==null) {
			cfDNASample=this.getNgs_library_cfdna_input();
			if(cfDNASample!=null) cfDNASample=cfDNASample.replace("ng", "");
		}
		return cfDNASample;
	}
	
	public float getGreenFloat() {
		if(greenFloat==-1) setCfdnaInputValues();
		return greenFloat;
	}
	
	public float getRedFloat() {
		if(redFloat==-1) setCfdnaInputValues();
		return redFloat;
	}
	
	public float getValueFloat() {
		if(valueFloat==-1) setCfdnaInputValues();
		return valueFloat;
	}
	
	public String getCfDNAInputColour() {
		if(cfDNAInputColour==null) setCfdnaInputValues();
		return cfDNAInputColour;
	}
	
	public float getGreenAvFloat() {
		if(greenAvFloat==-1)setAverageReadDepthcolourKeys();
		return greenAvFloat;
	}
	
	public float getRedAvFloat() {
		if(redAvFloat==-1)setAverageReadDepthcolourKeys();
		return redAvFloat;
	}
	
	public float getValueAvFloat() {
		if(valueAvFloat==-1)setAverageReadDepthcolourKeys();
		return valueAvFloat;
	}
	
	public String getAverageReadDepthColour() {
		if(averageReadDepthColour==null)setAverageReadDepthcolourKeys();
		return averageReadDepthColour;
	}
	
	
	public String getSpecimenDateFormatted1() {
		if (specimenDateFormatted1==null) {
			if(this.getSpecimen_date()!=null) {
				DateFormat dfspecimenDate1 = new SimpleDateFormat("dd/MM/yyyy");
				specimenDateFormatted1 = dfspecimenDate1.format(this.getSpecimen_date());
			} else {
				specimenDateFormatted1 = "";
			}
		}
		return specimenDateFormatted1;
	}

	public String getAverageReadDepthColourKeyGreen() {
		if(averageReadDepthColourKeyGreen==null) {
			setAverageReadDepthcolourKeys();
		}
		return averageReadDepthColourKeyGreen;
	}

	public String getAverageReadDepthColourKeyYellow() {
		if(averageReadDepthColourKeyYellow==null) {
			setAverageReadDepthcolourKeys();
		}
		return averageReadDepthColourKeyYellow;
	}

	public String getAverageReadDepthColourKeyRed() {
		if(averageReadDepthColourKeyRed==null) {
			setAverageReadDepthcolourKeys();
		}
		return averageReadDepthColourKeyRed;
	}
	
	public String getNgsSampleType() {
		return this.getPanel_name();
	}
	
	public void setTumourPanelName(String panel) {
		this.tumourPanelName=panel;
	}
	
	public String getTumourGenePanel() {
		return tumourPanelName;
	}
	
	public Map<String,Object> getNgsLibMap(){
		Map<String,Object> runCombo=new HashMap<String,Object>();
		Map<String,String> ngsLib= new HashMap<String,String>();
		ngsLib.put("measurement_gene_panel_id", Integer.toString(this.getMeasurement_gene_panel_id()));
		ngsLib.put("cfDNASample", this.getCfDNASample());
		ngsLib.put("cfDNAInputColour", this.getCfDNAInputColour());
		ngsLib.put("averageReadDepth", this.getAverage_read_depth());
		ngsLib.put("averageReadDepthColourKey", this.getAverage_read_depth_colour_key());
		ngsLib.put("averageReadDepthColourKeyGreen", this.getAverageReadDepthColourKeyGreen());
		ngsLib.put("averageReadDepthColourKeyYellow", this.getAverageReadDepthColourKeyYellow());
		ngsLib.put("averageReadDepthColourKeyRed", this.getAverageReadDepthColourKeyRed());
		ngsLib.put("averageReadDepthColour", this.getAverageReadDepthColour());
		ngsLib.put("cfdnaInputColourKeyGreen", this.getCfdnaInputColourKeyGreen());
		ngsLib.put("cfdnaInputColourKeyYellow", this.getCfdnaInputColourKeyYellow());
		ngsLib.put("cfdnaInputColourKeyRed", this.getCfdnaInputColourKeyRed());
		ngsLib.put("ngsRun", this.getNgs_run());
		ngsLib.put("ngsSampleType", this.getNgsSampleType());
		ngsLib.put("cfdnaInputColourKey", this.getCfdna_input_colour_key());
		ngsLib.put("bioinfPipeline", this.getBioinformatics_pipeline());
		ngsLib.put("levelOfDetection", this.getLevel_of_detection());
		ngsLib.put("exploratoryComment", this.getExploratory_comment());
		ngsLib.put("ngsComment", this.getNgs_comment());
		runCombo.put("ngsLib", ngsLib);
		runCombo.put("analysisFailed", this.isAnalysis_failed());
		runCombo.put("noMutationsFound", this.isNo_mutations_found());
		runCombo.put("showNilResult", "true");
		return runCombo;
	}

}
