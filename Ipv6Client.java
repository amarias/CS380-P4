import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Ipv6Client {

	public static void main(String[] args) {
		try {
			Socket socket = new Socket("codebank.xyz", 38004);

			// IPv6 Header
			int versionTrafficAndFlowLabel = 0x60000000;
			short payload = 1; // data size
			byte nextHeader = 17; // UDP
			byte hopLimit = 20;
			// Prefix: 0:0:0:0:0:FFFF
			long srcAddrFirstHalf = 0;
			int srcAddrPrefix = 0x0000FFFF;
			int srcAddr = 0;
			long destAddrFirstHalf = 0;
			int destAddrPrefix = 0x0000FFFF;
			byte[] destAddr = socket.getInetAddress().getAddress(); // 52.37.88.154:38004

			// Send packets
			for (int i = 0; i < 12; i++) {

				payload = (short) (payload << 1);
				System.out.println("Data length: " + payload);

				byte[] p = new byte[payload];
				for (int k = 0; k < payload; k++) {
					p[i] = 0;
				}

				// create packet
				ByteBuffer bb = ByteBuffer.allocate(40 + payload);
				bb.putInt(versionTrafficAndFlowLabel);
				bb.putShort(payload);
				bb.put(nextHeader);
				bb.put(hopLimit);
				bb.putLong(srcAddrFirstHalf);
				bb.putInt(srcAddrPrefix);
				bb.putInt(srcAddr);
				bb.putLong(destAddrFirstHalf);
				bb.putInt(destAddrPrefix);
				bb.put(destAddr);
				bb.put(p);

				socket.getOutputStream().write(bb.array());

				// Get magic number
				int response = socket.getInputStream().read();
				for (int j = 0; j < 3; j++) {
					response <<= 8;
					response |= socket.getInputStream().read();
				}

				System.out.println(String.format("Response: 0x%X\n", response));
			}

			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
