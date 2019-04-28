package gov.agilemeridian.gnss;

import com.google.protobuf.FloatValue;
import com.google.protobuf.Int32Value;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Timestamp;
import gov.agilemeridian.util.CodecUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.zip.Deflater;

import static org.junit.Assert.*;

public class GnssReportProtoTest
{

    @Test
    public void testSer2()
    {

        System.out.println("TESTING!!!!");
        GnssReportProto.GnssReport.Builder report = GnssReportProto.GnssReport.newBuilder();

        //Legacy for determing serialization size performance. GPB does RLE on floating point.
        int lat = (int) (38.89874 * 10000000.0);
        int lon = (int) (-77.03768 * 10000000.0);

        double latD = lat * 0.0000001;
        double lonD = lon * 0.0000001;

        report.setLatLng(GnssReportProto.LatLng.newBuilder().setLatitude(38.89874f).setLongitude(-77.03768f).build());

        report.setCallsign("TommyKnocker");
        report.setAltitude(FloatValue.newBuilder().setValue(3300f).build());
        long millis = 3;
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(millis / 1000)
                .setNanos((int) ((millis % 1000) * 1000000)).build();
        report.setSysTime(timestamp);

        Random rand = new Random();
        IntStream.rangeClosed(1, 1).forEach(i ->
                {
                    GnssReportProto.GNSSMeasurement.Builder measure = GnssReportProto.GNSSMeasurement.newBuilder();


                    measure.setAgcdB( FloatValue.newBuilder().setValue(3.3f).build());
                    measure.setRfStatus(GnssReportProto.RfStatus.SPOOFED);
                    //measure.setCarrierFrequencyHz(rand.nextLong());
                    measure.setSvId( Int32Value.newBuilder().setValue(4).build());
                    measure.setHDop( FloatValue.newBuilder().setValue(1.3f).build());
                    //measure.setVDop(rand.nextFloat());
                    measure.setConstellation(GnssReportProto.Constellation.GPS);
                    measure.setCn0DbHz( FloatValue.newBuilder().setValue(33.9f).build());
                    //measure.setSNrdB(rand.nextFloat());
                    report.addMeasurements(measure);
                }
        );

        GnssReportProto.PacketWrapper wrapperToEncode = GnssReportProto.PacketWrapper.newBuilder().setGnssReport(report).build();


        byte[] original = wrapperToEncode.toByteArray();


        String javaEncoded = CodecUtils.base16Encode(original);
        byte[] serialized = CodecUtils.base16Decode(javaEncoded);

        boolean equals = Arrays.equals(original, serialized);


        try
        {
            GnssReportProto.PacketWrapper wrapperDeserialized = GnssReportProto.PacketWrapper.parseFrom(serialized);


            switch (wrapperDeserialized.getPayloadCase()){
                case GNSSREPORT:
                    wrapperDeserialized.getGnssReport();
                    assertTrue(wrapperDeserialized.getGnssReport().hasLatLng());
                    assertFalse(wrapperDeserialized.getGnssReport().getMeasurements(0).hasSNrdB());
                    assertTrue(wrapperDeserialized.getGnssReport().getMeasurements(0).hasCn0DbHz());
                    assertEquals(wrapperDeserialized.getGnssReport().getMeasurements(0).getCn0DbHz().getValue(),33.9f, 0.0);
                    break;
                case PAYLOAD_NOT_SET:
                default:
                    throw new InvalidProtocolBufferException("Invalid payload definition in GNSS Packet");

            }
        } catch (InvalidProtocolBufferException e)
        {
            e.printStackTrace();
        }

        Deflater compresser = new Deflater();
        compresser.setInput(serialized);
        compresser.finish();
        byte[] output = new byte[serialized.length];
        int compressedDataLength = compresser.deflate(output);
        compresser.end();

//        assertTrue(serialized.length < 260);

        System.out.println(serialized.length);

        byte[] bytesFromGoTenna = hexStringToByteArray(GoTennaSent);
        try
        {



            GnssReportProto.GnssReport goTennaDeserialized = GnssReportProto.GnssReport.parseFrom(bytesFromGoTenna);


            System.out.println(goTennaDeserialized);

        } catch (InvalidProtocolBufferException e)
        {
            e.printStackTrace();
        }


    }


    private static String GoTennaSent = "0A1163616E6172795F74616E6F6F6B695F303112001DFCD1B444220B08EC82C1DD0510D0CCBF7D2A04080118032A060802100118032A06080110021801";





    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHexString(byte[] bytes)
    {
        if (bytes == null)
        {
            return "";
        }

        char[] hexChars = new char[bytes.length * 2];

        for (int j = 0; j < bytes.length; j++)
        {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }

        return new String(hexChars);
    }
    public static byte[] hexStringToByteArray(String string)
    {
        int len = string.length();
        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2)
        {
            data[i / 2] = (byte) ((Character.digit(string.charAt(i), 16) << 4) + Character.digit(string.charAt(i + 1), 16));
        }

        return data;
    }


}