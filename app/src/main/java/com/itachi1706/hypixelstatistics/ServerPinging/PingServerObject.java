package com.itachi1706.hypixelstatistics.ServerPinging;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

/**
 * Created by Kenneth on 7/2/2015, 5:34 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.ServerPinging
 */
@SuppressWarnings("unused")
public class PingServerObject {

    private InetSocketAddress host;
    private int timeout = 7000;
    private Gson gson = new Gson();

    //Set Minecraft Server Address
    public void setAddress(InetSocketAddress host) {
        this.host = host;
    }

    //Get Minecraft Server Address
    public InetSocketAddress getAddress() {
        return this.host;
    }

    //Set Connect Timeout
    void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    //Get Connect Timeout
    int getTimeout() {
        return this.timeout;
    }

    public int readVarInt(DataInputStream in) throws IOException {
        int i = 0;
        int j = 0;
        while (true) {
            int k = in.readByte();
            i |= (k & 0x7F) << j++ * 7;
            if (j > 5) throw new RuntimeException("VarInt too big");
            if ((k & 0x80) != 128) break;
        }
        return i;
    }

    public void writeVarInt(DataOutputStream out, int paramInt) throws IOException {
        while (true) {
            if ((paramInt & 0xFFFFFF80) == 0) {
                out.writeByte(paramInt);
                return;
            }

            out.writeByte(paramInt & 0x7F | 0x80);
            paramInt >>>= 7;
        }
    }

    public StatusResponse fetchData() throws IOException {

        Socket socket = new Socket();
        OutputStream outputStream;
        DataOutputStream dataOutputStream;
        InputStream inputStream;
        InputStreamReader inputStreamReader;

        socket.setSoTimeout(this.timeout);

        socket.connect(host, timeout);

        outputStream = socket.getOutputStream();
        dataOutputStream = new DataOutputStream(outputStream);

        inputStream = socket.getInputStream();
        inputStreamReader = new InputStreamReader(inputStream);

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream handshake = new DataOutputStream(b);

        //To build packet you need in order: Protocol Version (VarInt), Server Address (String), Server Port (Short), Next State (VarInt)
        //More info at http://wiki.vg/Server_List_Ping

        //Server Handshake Packet
        handshake.writeByte(0x00); //packet id for handshake
        writeVarInt(handshake, 4); //protocol version (4 as of MC 1.7.2)
        //writeVarInt(handshake, this.host.getHostName().length()); //host length
        String hostnamer = new PingServerObjectHost().onPostExecute(this.host);
        writeVarInt(handshake, hostnamer.length()); //host length
        //handshake.writeBytes(this.host.getHostName()); //host string
        handshake.writeBytes(hostnamer); //host strin
        handshake.writeShort(host.getPort()); //port
        writeVarInt(handshake, 1); //state (1 for handshake)

        writeVarInt(dataOutputStream, b.size()); //prepend size
        dataOutputStream.write(b.toByteArray()); //write handshake packet


        //Server Request Packet
        dataOutputStream.writeByte(0x01); //size is only 1 (Size of Server Request Packet)
        dataOutputStream.writeByte(0x00); //packet id for ping

        //Status Response Packet
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        int size = readVarInt(dataInputStream); //size of packet
        int id = readVarInt(dataInputStream); //packet id
        Log.i("1.7 Server Ping", "DEBUG: Packet Size of Status Response Packet = " + size);
        //System.out.println("DEBUG: Packet Size of Status Response Packet = " + size);

        //Check if Packet ID is 0x00 (Status Response)
        if (id == -1) {
            socket.close();
            throw new IOException("Premature end of stream.");
        }

        if (id != 0x00) { //we want a status response
            socket.close();
            throw new IOException("Invalid packetID");
        }
        int length = readVarInt(dataInputStream); //length of json string

        //Check if json string length actually has stuff
        if (length == -1) {
            socket.close();
            throw new IOException("Premature end of stream.");
        }

        if (length == 0) {
            socket.close();
            throw new IOException("Invalid string length.");
        }

        //Get entire json
        byte[] in = new byte[length];
        dataInputStream.readFully(in);  //read json string
        String json = new String(in);

        //Ping Packet
        long now = System.currentTimeMillis();
        dataOutputStream.writeByte(0x09); //size of packet (Size of Ping Packet [9 total. 1 for packet ID, 8 for the time itself])
        dataOutputStream.writeByte(0x01); //0x01 for ping
        dataOutputStream.writeLong(now); //time!?

        //Server Ping Response
        readVarInt(dataInputStream);
        id = readVarInt(dataInputStream);
        //Check if Packet ID is 0x01 (Ping Response)
        if (id == -1) {
            socket.close();
            throw new IOException("Premature end of stream.");
        }

        if (id != 0x01) {
            socket.close();
            throw new IOException("Invalid packetID");
        }
        long pingtime = dataInputStream.readLong(); //read response

        //Parse Json
        StatusResponse response = gson.fromJson(json, StatusResponse.class);
        response.setTime((int) (now - pingtime));	//Check and print out latency value

        //Close all query streams to the server
        dataOutputStream.close();
        outputStream.close();
        inputStreamReader.close();
        inputStream.close();
        socket.close();

        return response;
    }

    public class PingServerObjectHost extends AsyncTask<InetSocketAddress, Void, String> {
        // Do the long-running work in here
        @Override
        protected  String doInBackground(InetSocketAddress... addr) {
            // Get Length
            return addr[0].getHostName();
        }

        public String onPostExecute(InetSocketAddress host) {
            return host.getHostName();
        }
    }

    //Status Response Object Class
    public class StatusResponse {
        private String description;
        private Players players;
        private Version version;
        private String favicon;
        private int time;

        public StatusResponse(String desc){
            description=desc;
        }

        public String getDescription() {
            return description;
        }

        public Players getPlayers() {
            return players;
        }

        public Version getVersion() {
            return version;
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }

    }

    //Players Object Class (List)
    public class Players {
        private int max;
        private int online;
        private List<Player> sample;

        public int getMax() {
            return max;
        }

        public int getOnline() {
            return online;
        }

        public List<Player> getSample() {
            return sample;
        }
    }

    //Player Object Class (Single)
    public class Player {
        private String name;
        private String id;

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }

    }

    //Version Number Object Class
    public class Version {
        private String name;
        private String protocol;

        public String getName() {
            return name;
        }

        public String getProtocol() {
            return protocol;
        }
    }

}
