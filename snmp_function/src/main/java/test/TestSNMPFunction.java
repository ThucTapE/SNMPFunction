package test;

import java.util.Map;

import org.snmp4j.CommunityTarget;

import Eastgate.snmp_function.SNMPFunction;

public class TestSNMPFunction {
	public static void main(String[] args) throws Exception {
		CommunityTarget target = SNMPFunction.initTarget("209ijvfwer0df92jd", "10.10.1.94", "161", "version2c");
		Map<String, String> result1 = SNMPFunction.snmpGetNext(target, ".1.3.6.1.2.1.1.1.0");
		if (result1 != null)
			for (Map.Entry<String, String> entry : result1.entrySet()) {
				System.out.println(entry.getKey() + " ---- " + entry.getValue());
			}
		Map<String, String> result = SNMPFunction.snmpGet(target, ".1.3.6.1.2.1");
		if (result != null)
			for (Map.Entry<String, String> entry : result.entrySet()) {
				System.out.println(entry.getKey() + " ---- " + entry.getValue());
			}
		// Map<String, String> result = doWalk(".1.3.6.1.2.1", target); // ifTable,
		// mib-2 interfaces
		// snmpwalk
		/*
		 * for (Map.Entry<String, String> entry : result.entrySet()) {
		 * System.out.println( entry.getKey() +" ---- "+entry.getValue());
		 * 
		 * }
		 * 
		 * 
		 */
		// test get
		// snmpGet(target,".1.3.6.1.2.1.6.2.0");

		// test getNext
		// snmGetNext(target, ".1.3.6.1.2.1.6.2.0");

		// test
		// snmpSet(target, ".1.3.6.1.2.1.6.2.0", "200");

		// test getBulk
		/*
		 * VariableBinding[] array = {new VariableBinding(new OID(".1.3.6.1.2.1.7")),
		 * new VariableBinding(new OID(".1.3.6.1.2.1.7.1.0")), // new
		 * VariableBinding(new OID("1.3.6.1.4.1.2000.1.3.1.1.10")), //new
		 * VariableBinding(new OID("1.3.6.1.4.1.2000.1.2.5.1.19")) }; Vector<? extends
		 * VariableBinding> vbs = getBulk(target, array); if(vbs == null)
		 * System.out.println("Time out"); else { for (VariableBinding vb : vbs) {
		 * System.out.println(vb.getVariable().toString()); } }
		 */

		// test snmpset

		// snmpGet(target,".1.3.6.1.2.1.1.6.0");
		// if(snmpSet(target, ".1.3.6.1.2.1.1.6.0", "Hello") == true)
		// snmpGet(target,".1.3.6.1.2.1.1.6.0");
		// else
		// System.out.println("Error");
	}
}
