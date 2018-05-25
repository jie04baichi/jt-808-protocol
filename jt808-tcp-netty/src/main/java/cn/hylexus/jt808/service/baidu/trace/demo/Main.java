package cn.hylexus.jt808.service.baidu.trace.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hylexus.jt808.service.baidu.trace.LBSTraceClient;
import cn.hylexus.jt808.service.baidu.trace.api.track.AddPointsRequest;
import cn.hylexus.jt808.service.baidu.trace.api.track.UploadResponse;
import cn.hylexus.jt808.service.baidu.trace.model.CoordType;
import cn.hylexus.jt808.service.baidu.trace.model.LatLng;
import cn.hylexus.jt808.service.baidu.trace.model.OnUploadListener;
import cn.hylexus.jt808.service.baidu.trace.model.TrackPoint;


public class Main {

	private static Logger logger = LoggerFactory.getLogger(Main.class);
	
    private static AtomicLong mSequenceGenerator = new AtomicLong();

    private static AtomicInteger successCounter = new AtomicInteger();

    private static AtomicInteger failedCounter = new AtomicInteger();

    public static void main(String[] args) throws ExecutionException {
        LBSTraceClient client = LBSTraceClient.getInstance();
        client.init();
        client.start();

        client.registerUploadListener(new OnUploadListener() {

            @Override
            public void onSuccess(long responseId) {
            	logger.info("上传成功 : " + responseId + ", successCounter : " + successCounter.incrementAndGet());
            }

            @Override
            public void onFailed(UploadResponse response) {
            	logger.info("上传失败 : " + response.getResponseID() + ", failedCounter : "
                        + failedCounter.incrementAndGet() + ", " + response);
            }
        });

        List<TrackPoint> trackPoints = new ArrayList<TrackPoint>();
        for (int i = 1; i <= 50; ++i) {
            TrackPoint trackPoint = new TrackPoint(new LatLng(40.05 + i / 100.0, 116.31), CoordType.bd09ll, 30,
                    System.currentTimeMillis() / 1000 + i, 4, 20, 40, null, null);
            trackPoints.add(trackPoint);
        }
        
        for (int i = 0; i < 100; i++) {
            Map<String, List<TrackPoint>> trackPointMap = new HashMap<String, List<TrackPoint>>();
            trackPointMap.put("batch_upload_" + i, trackPoints);
            AddPointsRequest request = new AddPointsRequest(mSequenceGenerator.incrementAndGet(),
                    "aElWRH5ayr3b6fGBlyjZH0z9o857Y8aI", 162014, trackPointMap);
            // 批量添加轨迹点
            client.addPoints(request);
        }
        /*
        AddPointRequest request1 = new AddPointRequest(mSequenceGenerator.incrementAndGet(),"aElWRH5ayr3b6fGBlyjZH0z9o857Y8aI" ,162014,
        "batch_upload_1", trackPoints.get(0));
        client.addPoint(request1);*/
        //
        // AddPointsRequest request2 = new AddPointsRequest(101,
        // "4NEAN17DpkroLCVwZPg21EIQ0KsxGt3E", 137062,
        // trackPointMap);
        // instance.addPoints(request2);

    }

}
