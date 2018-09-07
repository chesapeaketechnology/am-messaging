package gov.agilemeridian.gnss;

import com.google.protobuf.Timestamp;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class GnssReportProtoTest
{

    @Test
    public void testSerialization()
    {
        GnssReportProto.GnssReport.Builder report = GnssReportProto.GnssReport.newBuilder();

        report.setLatLng(GnssReportProto.LatLng.newBuilder().setLatitude(80).setLongitude(-117).build());

        long millis = System.currentTimeMillis();
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(millis / 1000)
                .setNanos((int) ((millis % 1000) * 1000000)).build();
        report.setSysTime(timestamp);


        byte[] serialized = report.build().toByteArray();

        assertTrue(serialized.length < 260);

        System.out.println(serialized.length);
    }
}