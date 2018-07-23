package Eastgate.snmp_function;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeUtils;

public class SNMPFunction {
public static void main(String[] args) throws Exception {
		
		CommunityTarget target = new CommunityTarget();
		target.setCommunity(new OctetString("idv90we3rnov90wer"));
		target.setAddress(GenericAddress.parse("udp:10.10.1.94/161")); // supply your own IP and port
		target.setRetries(2);
		target.setTimeout(1500);
		target.setVersion(SnmpConstants.version2c);
		
		
		//Map<String, String> result = doWalk(".1.3.6.1.2.1", target); // ifTable, mib-2 interfaces
		//snmpwalk
		/*
		for (Map.Entry<String, String> entry : result.entrySet()) {
			System.out.println( entry.getKey() +" ---- "+entry.getValue());
			
		}
		*/
		//test get
		//snmpGet(target,".1.3.6.1.2.1.6.2.0");
		
		//test getNext
		//snmGetNext(target, ".1.3.6.1.2.1.6.2.0");
		
		//test 
		//snmpSet(target, ".1.3.6.1.2.1.6.2.0", "200");
		
		//test getBulk
		/*
		VariableBinding[] array = {new VariableBinding(new OID(".1.3.6.1.2.1.7")),
                new VariableBinding(new OID(".1.3.6.1.2.1.7.1.0")),
               // new VariableBinding(new OID("1.3.6.1.4.1.2000.1.3.1.1.10")),
                //new VariableBinding(new OID("1.3.6.1.4.1.2000.1.2.5.1.19"))
                };
		Vector<? extends VariableBinding> vbs = getBulk(target, array);
		if(vbs == null) 
			System.out.println("Time out");
		else {
			for (VariableBinding vb : vbs) {
	            System.out.println(vb.getVariable().toString());
	        }
		}
		*/
		
		// test snmpset
		snmpGet(target,".1.3.6.1.2.1.1.6.0");
		if(snmpSet(target, ".1.3.6.1.2.1.1.6.0", "Hello") == true)
			snmpGet(target,".1.3.6.1.2.1.1.6.0");
		else 
			System.out.println("Error");
	}
	public static boolean snmpSet(Target target, String oid, String value) throws Exception {
		TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
		Snmp snmp = new Snmp(transport);
		transport.listen();
		
		PDU pdu = new PDU();
	    pdu.add(new VariableBinding(new OID(oid), new OctetString(value)));
	    pdu.setType(PDU.SET);
	    pdu.setRequestID(new Integer32(1));
	    
	    ResponseEvent event = snmp.send(pdu, target);
	    if (event != null) {
	        pdu = event.getResponse();
	        if (pdu.getErrorStatus() == PDU.noError) {
	        	 snmp.close();
	        	return true;
	          //System.out.println("SNMPv3 SET Successful!");
	        } else {
	        	 snmp.close();
	        	return false;
	          //System.out.println("SNMPv3 SET Unsuccessful.");
	        }
	    } else {
	    	 snmp.close();
	    	return false;
	      //System.out.println("SNMP send unsuccessful.");
	    }  
	}
	public static void snmpGet( Target target, String oid) throws IOException {
		TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
		Snmp snmp = new Snmp(transport);
		transport.listen();
		
	 // Create the PDU object
	    PDU pdu = new PDU();
	    pdu.add(new VariableBinding(new OID(oid)));
	    pdu.setType(PDU.GET);
	    pdu.setRequestID(new Integer32(1));

	    
	    System.out.println("Sending Request to Agent...");
	    ResponseEvent response = snmp.get(pdu, target);

	    // Process Agent Response
	    if (response != null)
	    {
	      System.out.println("Got Response from Agent");
	      PDU responsePDU = response.getResponse();

	      if (responsePDU != null)
	      {
	        int errorStatus = responsePDU.getErrorStatus();
	        int errorIndex = responsePDU.getErrorIndex();
	        String errorStatusText = responsePDU.getErrorStatusText();

	        if (errorStatus == PDU.noError)
	        {
	          System.out.println("Snmp Get Response = " + responsePDU.getVariableBindings());
	        }
	        else
	        {
	          System.out.println("Error: Request Failed");
	          System.out.println("Error Status = " + errorStatus);
	          System.out.println("Error Index = " + errorIndex);
	          System.out.println("Error Status Text = " + errorStatusText);
	        }
	      }
	      else
	      {
	        System.out.println("Error: Response PDU is null");
	      }
	    }
	    else
	    {
	      System.out.println("Error: Agent Timeout... ");
	    }
	    snmp.close();	    	
	}
	public static void snmGetNext(Target target, String oid) throws IOException{
		TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
		Snmp snmp = new Snmp(transport);
		transport.listen();
		
		// Create the PDU object
	    PDU pdu = new PDU();
	    pdu.add(new VariableBinding(new OID(oid))); //Querying GetNext of sysDescr will get the sysObjectID OID value
	    pdu.setRequestID(new Integer32(1));
	    pdu.setType(PDU.GETNEXT);

	    
	    ResponseEvent response = snmp.getNext(pdu, target);

	    // Process Agent Response
	    if (response != null)
	    {
	      System.out.println("\nResponse:\nGot GetNext Response from Agent...");
	      PDU responsePDU = response.getResponse();

	      if (responsePDU != null)
	      {
	        int errorStatus = responsePDU.getErrorStatus();
	        int errorIndex = responsePDU.getErrorIndex();
	        String errorStatusText = responsePDU.getErrorStatusText();

	        if (errorStatus == PDU.noError)
	        {
	          System.out.println("Snmp GetNext Response for sysObjectID = " + responsePDU.getVariableBindings());
	        }
	        else
	        {
	          System.out.println("Error: Request Failed");
	          System.out.println("Error Status = " + errorStatus);
	          System.out.println("Error Index = " + errorIndex);
	          System.out.println("Error Status Text = " + errorStatusText);
	        }
	      }
	      else
	      {
	        System.out.println("Error: GetNextResponse PDU is null");
	      }
	    }
	    else
	    {
	      System.out.println("Error: Agent Timeout... ");
	    }
	    snmp.close();
	}
	public static Map<String, String> doWalk(String tableOid, Target target) throws IOException {
		Map<String, String> result = new TreeMap<String, String>();
		TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
		Snmp snmp = new Snmp(transport);
		transport.listen();

		TreeUtils treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
		List<TreeEvent> events = treeUtils.getSubtree(target, new OID(tableOid));
		if (events == null || events.size() == 0) {
			System.out.println("Error: Unable to read table...");
			return result;
		}

		for (TreeEvent event : events) {
			if (event == null) {
				continue;
			}
			if (event.isError()) {
				System.out.println("Error: table OID [" + tableOid + "] " + event.getErrorMessage());
				continue;
			}

			VariableBinding[] varBindings = event.getVariableBindings();
			if (varBindings == null || varBindings.length == 0) {
				continue;
			}
			
			for (VariableBinding varBinding : varBindings) {
				if (varBinding == null) {
					continue;
				}
				
				result.put("." + varBinding.getOid().toString(), varBinding.getVariable().toString());
			}
		}
		snmp.close();
		return result;
	}
	public static Vector<? extends VariableBinding> getBulk(Target target, VariableBinding[] array)  throws IOException{
		TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
		Snmp snmp = new Snmp(transport);
		transport.listen();
		
		PDU pdu = new PDU();
	    pdu.setType(PDU.GETBULK);
	    pdu.setMaxRepetitions(1); 
	    pdu.setNonRepeaters(0);
	    pdu.addAll(array);

	    //pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.2000.1.2.5.1.3"))); 

	    ResponseEvent responseEvent = snmp.send(pdu, target);
	    PDU response = responseEvent.getResponse();

	    if (response == null) {
		    System.out.println("TimeOut...");
	    } else {
		    if (response.getErrorStatus() == PDU.noError) {
                Vector<? extends VariableBinding> vbs = response.getVariableBindings();
                return vbs;
                //for (VariableBinding vb : vbs) {
                  //  System.out.println(vb.getVariable().toString());
		        //}
		    } else {
		    	return null;
		        //System.out.println("Error:" + response.getErrorStatusText());
		    }
	    }
	    return null;
	}

}


