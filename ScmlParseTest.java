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

public class ScmlParseTest{
    public static void main(String[] args){
	//Log log = LogHelper.getLog();
        SCXML scxml = null;
        SCXML scxml1 = null;
	boolean simul = false;
	int i =0, j=0;
	String scxmlPath = "/home/subhajitsidhanta/Dropbox/Documents/GitHub/QORSensitiveDesign/VRDesignIOTS.scxml";
	String scxmlPath1 = "/home/subhajitsidhanta/Dropbox/Documents/GitHub/QORSensitiveDesign/VRDesignIOTS1.scxml";
	try{
          InputSource source = new InputSource(new BufferedReader(new FileReader(scxmlPath)));	  
          scxml = SCXMLParser.parse(source, new SimpleErrorHandler());

	  InputSource source1 = new InputSource(new BufferedReader(new FileReader(scxmlPath1)));	  
          scxml1 = SCXMLParser.parse(source1, new SimpleErrorHandler()); 
	  String initstate = scxml.getInitial();
	  String initstate1 = scxml1.getInitial();

	  Map targList = scxml.getChildren();
 	  Map targList1 = scxml1.getChildren();
          //SCXML config = null;
          //Map targets = scxml.getTargets();System.out.println("***state:-"+scxml1.getTargets()+" size:"+targets.size()	);     
          //for ( Object key : targets.keySet() ) {
    		//System.out.println( "***Key:="+(String)key );
	 // }
     
          //for (int k = 0; k < targets.size(); k++) {
                    /*TransitionTarget initialState = (TransitionTarget)
                        targets.get((String)key);
                    System.out.println("***=initialState"+initialState);*/
                    /*if (!SCXMLHelper.isDescendant(initialState, s)) {
                        logAndThrowModelError(ERR_STATE_BAD_INIT,
                            new Object[] {getStateName(s)});
                    }*/
          //}
          /*URL configUrl = ScmlParseTest.class.getClassLoader().getResource(scxmlPath);
    	  try{
    		 config = SCXMLParser.parse(configUrl, new ErrorHandler(){
    		    public void error(SAXParseException exception) throws SAXException {
    		        System.out.println("Couldn't parse SCXML Config (");// + configUrl + ")", exception);
    		    }*/

    		    /**
    		     * @see  ErrorHandler#fatalError(SAXParseException)
    		     */
    		    /*public void fatalError(SAXParseException exception) throws SAXException {
    		        System.out.println("Couldn't parse SCXML Config (");// + configUrl + ")", exception);
    		    }*/

    		    /**
    		     * @see  ErrorHandler#warning(SAXParseException)
    		     */
    		    /*public void warning(SAXParseException exception) throws SAXException {
    		        System.out.println("Couldn't parse SCXML Config (");// + configUrl + ")", exception);
    		    }
    		});
    	}catch(ModelException e){
    		System.out.println("Couldn't parse SCXML Config (");// + configUrl + ")", e);
    	}catch(SAXException e){
    		System.out.println("Couldn't parse SCXML Config (");// + configUrl + ")", e);
    	}catch(IOException e){
    		System.out.println("Couldn't parse SCXML Config");// (" + configUrl + ")", e);
    	}*/

          //Tracer tracer = new Tracer();
          //SCXMLExecutor	exec = new SCXMLExecutor();
	  Evaluator evaluator = new ShaleDialogELEvaluator();
          Context rootCtx = evaluator.newContext(null);
	  //SCXMLExecutor	exec = new SCXMLExecutor(exec.getEvaluator(),  exec.getEventdispatcher(),
				//exec.getErrorReporter());

          //exec.setStateMachine(scxml);

		// exec.addListener(scxml, tracer);

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
           // exec.getStateMachine();
          //SCXMLExecutionContext exctx = new SCXMLExecutionContext(exec, exec.getEvaluator(), exec.getEventdispatcher(), exec.getErrorReporter());
          //System.out.println("***transition"+exctx); 
	   String data = "";
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
			//System.out.println( "***valList:="+Arrays.toString(valList.toArray()) );

		        sourceState = stmc.getInitialTarget().getId();
			List<Transition> transitions = (List<Transition>) stmc.getInitialTarget().getTransitionsList();
			int k =0;
                        
                        
			while (k<2*lhm.size()){
			//while (transitions!= null && transitions.size()>0){
			
                        //List<Transition> transitions = (List<Transition>) stmc.getInitialTarget().getTransitionsList();
                        
			for (Transition transition : transitions) {
			    String event = transition.getEvent();
			    if(!eventList.contains(event))
                            	eventList.add(event);
		            TransitionTarget tarState = transition.getTarget();
                            destState = tarState.getId();
			    transitions = (List<Transition>) tarState.getTransitionsList();		    
		            //System.out.println(sourceState+","+event+","+destState+"/n");
			    //data = data + sourceState+","+event+","+destState+"\n";
			    data = data + sourceState+" "+event+" "+destState+"\n";
			    k++;
			   
			}
			 sourceState = destState;
			
			}
				} catch (ModelException e) {
					throw new IllegalArgumentException(
							"failed to create state machine instance", e);
	   			}

		   
	String output =  "/home/subhajitsidhanta/Dropbox/Documents/GitHub/QORSensitiveDesign/VRDesignIOTSEdges.sta";
	 
	  String data1 ="";
	   for (int counter = 0; counter < stateList.size(); counter++) { 
		data1 = null;		      
           	data1 = data.replace((String)stateList.get(counter),String.valueOf(counter)); 			data = data1;
      	   }   
           
  	   for (int counter = 0; counter < eventList.size(); counter++) { 
		data1 = null;				      
           	data1 = data.replace((String)eventList.get(counter),String.valueOf(counter)); 			data = data1;
      	   }
	   System.out.println( "***daat:="+data );
	   File file = new File(output); 
	   if (file.exists()) {
	     file.delete(); //you might want to check if delete was successfull
	   }
	   file.createNewFile();
	   try { 
            // create FileWriter object with file as parameter 
            FileWriter outputfile = new FileWriter(file); 
  
            
            outputfile.write(data1); 
  
            // closing writer connection 
            outputfile.close(); 
        } 
        catch (IOException e) { 
            // TODO Auto-generated catch block 
            e.printStackTrace(); 
        } 		
	
          //Map<String, State> allStates = exec.getStateMachine().getTargets();
          //List potentialTransitions = ((State) exec.getCurrentStatus()
				//.getStates().iterator().next()).getTransitionsList();    
          
 			
          /*State currentExecutionState = null;
          while (!exec.getCurrentStatus().isFinal())
	  {       
                  
		  currentExecutionState = ((State) exec.getCurrentStatus()
				.getStates().iterator().next());
                  @SuppressWarnings("unchecked")
  
		  List<Transition> currentTransitions = (List<Transition>)currentExecutionState.getTransitionsList();
		  for (Transition transition : currentTransitions) {
	   		//System.out.println("***transition.getEvent():-"+currentExecutionState);
		  }
		  currentExecutionState.getChildren();
                      TriggerEvent[] evts = {new TriggerEvent(null,
      TriggerEvent.SIGNAL_EVENT, null)};
    exec.triggerEvents();
          }	*/		
	  /* 	State state = allStates.get(idOfPersistedState);
	  if (state == null) {
	     throw new IllegalStateException("Unknown state "
						+ idOfPersistedState);
	  }*/

	  //got the state, now set it
			
	  //@SuppressWarnings("rawtypes")
	  //Set states = exec.getCurrentStatus().getStates();
	  //states.clear();
	  //states.add(state);

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
}
