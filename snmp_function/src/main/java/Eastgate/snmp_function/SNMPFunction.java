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
import org.snmp4j.security.SNMPv3SecurityModel;
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
	public static CommunityTarget initTarget(String community,String ip, String port,String version ) throws Exception {
		CommunityTarget target = new CommunityTarget();
		target.setCommunity(new OctetString(community));
		target.setAddress(GenericAddress.parse("udp:"+ip+"/"+port));
		target.setRetries(2);
		target.setTimeout(1500);
		if(version.equals("version1")) 
			target.setVersion(SnmpConstants.version1);
		else if(version.equals("version3"))
			target.setVersion(SnmpConstants.version3);
		else 
			target.setVersion(SnmpConstants.version2c);
		return target;
	}
	public static CommunityTarget initTargetDefaultCommunityString(String ip, String port, String version) throws Exception{
		CommunityTarget target = new CommunityTarget();
		target.setCommunity(new OctetString("public"));
		target.setAddress(GenericAddress.parse("udp:"+ip+"/"+port));
		target.setRetries(2);
		target.setTimeout(1500);
		if(version.equals("version1")) 
			target.setVersion(SnmpConstants.version1);
		else if(version.equals("version2c"))
			target.setVersion(SnmpConstants.version2c);
		else
			target.setVersion(SnmpConstants.version3);
		return target;
	}
	public static CommunityTarget initTargetDefaultVersion(String community,String ip, String port) throws Exception{
		CommunityTarget target = new CommunityTarget();
		target.setCommunity(new OctetString(community));
		target.setAddress(GenericAddress.parse("udp:"+ip+"/"+port));
		target.setRetries(2);
		target.setTimeout(1500);
		target.setVersion(SnmpConstants.version2c);
		return target;
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
				// System.out.println("SNMPv3 SET Successful!");
			} else {
				snmp.close();
				return false;
				// System.out.println("SNMPv3 SET Unsuccessful.");
			}
		} else {
			snmp.close();
			return false;
			// System.out.println("SNMP send unsuccessful.");
		}
	}

	public static Map<String, String> snmpGet(Target target, String oid) throws IOException {
		Map<String, String> result = new TreeMap<String, String>();
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
		if (response != null) {
			System.out.println("Got Response from Agent");
			PDU responsePDU = response.getResponse();
			if (responsePDU != null) {
				int errorStatus = responsePDU.getErrorStatus();
				int errorIndex = responsePDU.getErrorIndex();
				String errorStatusText = responsePDU.getErrorStatusText();

				if (errorStatus == PDU.noError) {
					// ?
					Vector<? extends VariableBinding> vbs = responsePDU.getVariableBindings();
					VariableBinding vb = vbs.firstElement();
					result.put(vb.getOid().toString(), vb.getVariable().toString());
					return result;
					// System.out.println("Snmp Get Response = " +
					// responsePDU.getVariableBindings());
				}
			}
		}
		snmp.close();
		return null;
	}

	public static Map<String, String> snmpGetNext(Target target, String oid) throws IOException {
		Map<String, String> result = new TreeMap();
		TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
		Snmp snmp = new Snmp(transport);
		transport.listen();

		// Create the PDU object
		PDU pdu = new PDU();
		pdu.add(new VariableBinding(new OID(oid))); // Querying GetNext of sysDescr will get the sysObjectID OID value
		pdu.setRequestID(new Integer32(1));
		pdu.setType(PDU.GETNEXT);

		ResponseEvent response = snmp.getNext(pdu, target);

		// Process Agent Response
		if (response != null) {
			System.out.println("\nResponse:\nGot GetNext Response from Agent...");
			PDU responsePDU = response.getResponse();

			if (responsePDU != null) {
				int errorStatus = responsePDU.getErrorStatus();
				int errorIndex = responsePDU.getErrorIndex();
				String errorStatusText = responsePDU.getErrorStatusText();

				if (errorStatus == PDU.noError) {
					Vector<? extends VariableBinding> vbs = responsePDU.getVariableBindings();
					VariableBinding vb = vbs.firstElement();
					result.put(vb.getOid().toString(), vb.getVariable().toString());
					snmp.close();
					return result;
					// System.out.println("Snmp GetNext Response for sysObjectID = " +
					// responsePDU.getVariableBindings());
				}
			}
		}
		snmp.close();
		return null;
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

	public static Map<String, String> getBulk(Target target, VariableBinding[] array) throws IOException {
		Map<String, String> result = new TreeMap<String, String>();
		TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
		Snmp snmp = new Snmp(transport);
		transport.listen();

		PDU pdu = new PDU();
		pdu.setType(PDU.GETBULK);
		pdu.setMaxRepetitions(1);
		pdu.setNonRepeaters(0);
		pdu.addAll(array);

		// pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.2000.1.2.5.1.3")));

		ResponseEvent responseEvent = snmp.send(pdu, target);
		PDU response = responseEvent.getResponse();

		if (response != null) {
			if (response.getErrorStatus() == PDU.noError) {
				Vector<? extends VariableBinding> vbs = response.getVariableBindings();

				for (VariableBinding vb : vbs) {
					// System.out.println(vb.getVariable().toString());
					result.put(vb.getOid().toString(), vb.getVariable().toString());
				}
				return result;
			}
		}
		return null;
	}

}
