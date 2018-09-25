package gov.agilemeridian.gnss;

import com.google.protobuf.Timestamp;
import org.junit.Test;

import java.util.Random;
import java.util.stream.IntStream;
import java.util.zip.Deflater;

import static org.junit.Assert.assertTrue;

public class GnssReportProtoTest
{

    @Test
    public void testSerialization()
    {
        GnssReportProto.GnssReport.Builder report = GnssReportProto.GnssReport.newBuilder();

        int lat = (int) (38.89874 * 100000.0);
        int lon = (int) (-77.03768 * 100000.0);

        report.setLatLng(GnssReportProto.LatLng.newBuilder().setLatitude(lat).setLongitude(lon).build());

        report.setCallsign("TommyKnocker");
        report.setAltitude(32000.4f);
        long millis = System.currentTimeMillis();
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(millis / 1000)
                .setNanos((int) ((millis % 1000) * 1000000)).build();
        report.setSysTime(timestamp);

        Random rand = new Random();
        IntStream.rangeClosed(1, 1).forEach(i ->
                {
                    GnssReportProto.GNSSMeasurement.Builder measure = GnssReportProto.GNSSMeasurement.newBuilder();

                    measure.setAgcdB(rand.nextFloat());
                    measure.setRfStatus(GnssReportProto.RfStatus.SPOOFED);
                    //measure.setCarrierFrequencyHz(rand.nextLong());
                    //measure.setSvId(rand.nextInt());
                    measure.setHDop(rand.nextFloat());
                    //measure.setVDop(rand.nextFloat());
                    measure.setConstellation(GnssReportProto.Constellation.GPS);
                    //measure.setCn0DbHz(rand.nextFloat());
                    //measure.setSNrdB(rand.nextFloat());
                    report.addMeasurements(measure);
                }
        );

        byte[] serialized = report.build().toByteArray();

        Deflater compresser = new Deflater();
        compresser.setInput(serialized);
        compresser.finish();
        byte[] output = new byte[serialized.length];
        int compressedDataLength = compresser.deflate(output);
        compresser.end();

        assertTrue(serialized.length < 260);

        System.out.println(serialized.length);
    }
}