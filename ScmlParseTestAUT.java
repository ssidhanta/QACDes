import java.io.IOException;
import java.net.URL;
import org.apache.commons.scxml.io.SCXMLParser;
import org.apache.commons.scxml.env.SimpleErrorHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import org.apache.commons.scxml.model.*;
import org.w3c.dom.*;
import org.apache.commons.scxml.SCXMLHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.scxml.io.SCXMLParser;
import org.apache.commons.scxml.model.ModelException;
import org.apache.commons.scxml.model.SCXML;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.apache.commons.scxml.env.Tracer;

import org.apache.commons.scxml.Context;
import org.apache.commons.scxml.SCXMLExecutor;
//import org.apache.commons.scxml2.SCXMLExecutionContext;
import org.apache.commons.scxml.SCXMLExpressionException;
import org.apache.commons.scxml.TriggerEvent;
import org.apache.commons.scxml.env.SimpleContext;
import org.apache.commons.scxml.env.SimpleDispatcher;
import org.apache.commons.scxml.env.Tracer;
import org.apache.commons.scxml.model.ModelException;
import org.apache.commons.scxml.model.SCXML;
import org.apache.commons.scxml.model.State;
import org.apache.commons.scxml.Evaluator;
import org.apache.shale.dialog.scxml.*;
import org.apache.commons.scxml.env.SimpleScheduler;
import org.apache.commons.scxml.EventDispatcher;
import org.apache.commons.scxml.invoke.SimpleSCXMLInvoker;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.scxml.Status;
import org.apache.commons.scxml.Step;
import java.util.Iterator;
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;

public class ScmlParseTestAUT{

    public static void main(String[] args){
	//Log log = LogHelper.getLog();
        SCXML scxml = null;
        SCXML scxml1 = null;
	boolean simul = false;
	int i =0, j=0;
	String scxmlPath = "/home/subhajitsidhanta/Dropbox/Documents/GitHub/QORSensitiveDesign/VRDesignIOTS.scxml";
	String scxmlPath1 = "/home/subhajitsidhanta/Dropbox/Documents/GitHub/QORSensitiveDesign/VRDesignSTED.scxml";
	try{
          InputSource source = new InputSource(new BufferedReader(new FileReader(scxmlPath)));	  
          scxml = SCXMLParser.parse(source, new SimpleErrorHandler());

	  InputSource source1 = new InputSource(new BufferedReader(new FileReader(scxmlPath1)));	  
          scxml1 = SCXMLParser.parse(source1, new SimpleErrorHandler()); 
	  String initstate = scxml.getInitial();
	  String initstate1 = scxml1.getInitial();

	  Map targList = scxml.getChildren();
 	  Map targList1 = scxml1.getChildren();
		   
	String output =  "/home/subhajitsidhanta/Dropbox/Documents/GitHub/QORSensitiveDesign/VRDesignIOTSEdges.aut";
	String output1 =  "/home/subhajitsidhanta/Dropbox/Documents/GitHub/QORSensitiveDesign/VRDesignSTED.aut";
	writeAUT(scxml, output);
	writeAUT(scxml1, output1); 
	   
	  if(initstate.equalsIgnoreCase(initstate1))
	  {
		  //simul = true;
		  for (Object targObject : targList.entrySet()) {
	            j=0;
		    Map.Entry targ = (Map.Entry) targObject;
	 
		    String currenttargName = (String) targ.getKey();
		    State currenttarg = (State) targ.getValue();
		    
		    //System.out.println("***currenttargeName:-"+currenttargName);
		    
	 	    for (Object targObject1 : targList1.entrySet()) {
	 	        if (j<i)
			   j++;
			else if (j==i){
		    	   Map.Entry targ1 = (Map.Entry) targObject1;
	 		
		    	   String currenttargName1 = (String) targ1.getKey();
		    	   State currenttarg1 = (State) targ1.getValue();
		    	   //System.out.println("***i:="+i+ " j:="+j+"  currenttargeName:-"+currenttargName+"  currenttargeName1:-"+currenttargName1);
			   if (currenttargName.equalsIgnoreCase(currenttargName1))
			      simul=true;
			   j++;
			}
		    }
		    i++;
		  }
	  }
	  else
		simul = false; 
	  //if (simul)
	 System.out.println("***simul:="+simul+"****scxml.getTargets()="+scxml.getTargets()+"***scxml1.getTargets()"+scxml1.getTargets());
        } catch (IOException ioe) {
	  System.out.println("***ioe error:-");

        } catch (SAXException se) {
	  System.out.println("***se error:-");

        } catch (ModelException me) {
	  System.out.println("***me error:-");

        }
    }

    static void writeAUT(SCXML scxml, String output)
    {
	Evaluator evaluator = new ShaleDialogELEvaluator();
          Context rootCtx = evaluator.newContext(null);
	 
	   Tracer trc = new Tracer();
           
            //System.out.println(SCXMLSerializer.serialize(scxml));
            SCXMLExecutor exec = new SCXMLExecutor(evaluator, null, trc);
            EventDispatcher ed = new SimpleScheduler(exec);
            exec.setEventdispatcher(ed);
            exec.setStateMachine(scxml);
            exec.addListener(scxml, trc);
            exec.registerInvokerClass("scxml", SimpleSCXMLInvoker.class);
            exec.setRootContext(rootCtx);
            List<String> eventList = new ArrayList<String>();
	    List<String> stateList = new ArrayList<String>();

	   String data = "";
	   List<Transition> transitions = null;
	   int k =0;
          try {
			exec.go();
                        
			Status currentStatus = new Status();
            		Step step = new Step(null, currentStatus);    
            		System.out.println("***step"+exec.getStateMachine().getStates().getClass().getSimpleName() +exec.getStateMachine().getInitialState()+exec.getStateMachine().getInitialTarget().getId());
			
		        String sourceState, destState ="";
			SCXML stmc = exec.getStateMachine();
			Map<Integer, State> lhm = stmc.getStates();
			int l = 0;
                        
                        Collection<State> values = lhm.values();
			for(State value : values){
                            stateList.add((String)value.getId());
			}
			

		        sourceState = stmc.getInitialTarget().getId();
			transitions = (List<Transition>) stmc.getInitialTarget().getTransitionsList();
			                       
			while (k<2*lhm.size()){
                        System.out.println( "***lhm.size():="+lhm.size()+"  k:"+k);
			for (Transition transition : transitions) {
			    String event = transition.getEvent();
			    if(!eventList.contains(event))
                            	eventList.add(event);
		            TransitionTarget tarState = transition.getTarget();
                            destState = tarState.getId();
			    transitions = (List<Transition>) 
tarState.getTransitionsList();		    

			    data = data + "(" + sourceState + ", " + event + ", " + destState + ")\n";
			    k++;
			   
			}
			 sourceState = destState;
			
	String data1 ="";
	   for (int counter = 0; counter < stateList.size(); counter++) { 
		data1 = null;		      
           	data1 = data.replace((String)stateList.get(counter),String.valueOf(counter)); 			data = data1;
      	   }   
           
  	   /*for (int counter = 0; counter < eventList.size(); counter++) { 
		data1 = null;				      
           	data1 = data.replace((String)eventList.get(counter),String.valueOf(counter)); 			data = data1;
      	   }*/
	   //System.out.println( "***daat:="+data );

	   String header ="des (0" + ", " + k + ", " + stateList.size() + ")\n";  
	   
	   File file = new File(output); 
	   if (file.exists()) {
	     file.delete(); //you might want to check if delete was successfull
	   }
	   file.createNewFile();
            // create FileWriter object with file as parameter 
            FileWriter outputfile = new FileWriter(file);            
            outputfile.write(header+data1); 
  
            // closing writer connection 
            outputfile.close(); 
        } } catch (ModelException e) {
					throw new IllegalArgumentException(
							"failed to create state machine instance", e);
	   			}
        catch (IOException e) { 
            // TODO Auto-generated catch block 
            e.printStackTrace(); 
        } 		
    }
}
