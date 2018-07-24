package test;

import java.io.IOException;

import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.PDU;
import org.snmp4j.smi.UdpAddress;

import Eastgate.snmp_function.TrapReceiver;

public class TestTrapReceiver implements CommandResponder {

	public static void main(String[] args) {
		TrapReceiver snmp4jTrapReceiver = new TrapReceiver();
		try {
			snmp4jTrapReceiver.listen(new UdpAddress("localhost/162"));
		} catch (IOException e) {
			System.err.println("Error in Listening for Trap");
			System.err.println("Exception Message = " + e.getMessage());
		}
	}

	public void processPdu(CommandResponderEvent event) {
		// TODO Auto-generated method stub
		PDU pdu = event.getPDU();
		if (pdu != null) {
			System.out.println("Trap Type = " + pdu.getType());
			System.out.println("Variables = " + pdu.getVariableBindings());
		}
	}

}
