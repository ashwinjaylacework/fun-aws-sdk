package net.lacework.fun.aws;

import com.amazonaws.Request;
import com.amazonaws.SdkClientException;
import com.amazonaws.handlers.RequestHandler2;
import com.amazonaws.http.HttpResponse;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetBucketPolicyRequest;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.TeeInputStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by ashwinjayaprakash on 8/18/21.
 */
public class S3BucketDemo {
    public static void main(String[] args) throws IOException {
        File dirRoot = new File("data");
        FileUtils.deleteDirectory(dirRoot);
        dirRoot.mkdirs();

        final AtomicReference<String> filePath = new AtomicReference<>();
        final RequestHandler2 requestHandler = newTeeingHandler(filePath);
        final AmazonS3 s3 = AmazonS3ClientBuilder
                .standard()
                .withRequestHandlers(requestHandler)
                .withRegion(Regions.DEFAULT_REGION)
                .build();

        filePath.set("data/s3-list-buckets.xml");
        List<Bucket> buckets = s3.listBuckets();
        System.out.printf("Downloaded file [%s] of size [%d]", filePath.get(), new File(filePath.get()).length());

        System.out.println("Your Amazon S3 buckets are:");
        for (Bucket b : buckets) {
            System.out.println(" - " + b.getName());

            filePath.set(String.format("data/s3-bucket-policy-%s.json", b.getName()));
            try {
                s3.getBucketPolicy(new GetBucketPolicyRequest(b.getName()));
                System.out.printf("Downloaded file [%s] of size [%d]", filePath.get(), new File(filePath.get()).length());
            } catch (SdkClientException e) {
                e.printStackTrace();
            }
        }
    }

    private static RequestHandler2 newTeeingHandler(final AtomicReference<String> filePath) {
        return new RequestHandler2() {
            @Override
            public HttpResponse beforeUnmarshalling(Request<?> request, HttpResponse httpResponse) {
                try {
                    FileOutputStream fos = new FileOutputStream(filePath.get());
                    TeeInputStream tee = new TeeInputStream(httpResponse.getContent(), fos, true);
                    httpResponse.setContent(tee);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                return super.beforeUnmarshalling(request, httpResponse);
            }
        };
    }
}
