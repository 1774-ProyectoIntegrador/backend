package proyecto.dh.resources.product.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import proyecto.dh.resources.product.entity.ImageProduct;
import proyecto.dh.resources.product.entity.Product;
import proyecto.dh.resources.product.repository.ImageProductRepository;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

@Service
public class S3Service {

    private final AmazonS3 s3Client;
    private final String bucketName;
    private final ImageProductRepository imageProductRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public S3Service(@Value("${aws.accessKeyId}") String accessKeyId,
                     @Value("${aws.secretAccessKey}") String secretAccessKey,
                     @Value("${aws.s3.region}") String region,
                     @Value("${aws.s3.bucketName}") String bucketName,
                     ImageProductRepository imageProductRepository) {

        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKeyId, secretAccessKey);
        this.s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.fromName(region))
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();

        this.bucketName = bucketName;
        this.imageProductRepository = imageProductRepository;
    }

    private String generateRandomFileName() {
        byte[] randomBytes = new byte[16];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().encodeToString(randomBytes);
    }

    public ImageProduct uploadFile(MultipartFile file, Product product) throws IOException {
        String fileExtension = "";
        String originalFileName = file.getOriginalFilename();
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        String key = generateRandomFileName() + fileExtension;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead);
        s3Client.putObject(putObjectRequest);

        String url = s3Client.getUrl(bucketName, key).toString();

        ImageProduct imageProduct = new ImageProduct();
        imageProduct.setUrl(url);
        imageProduct.setFileName(key);
        imageProduct.setProduct(product);

        return imageProductRepository.save(imageProduct);
    }

    public void deleteFile(String key) {
        s3Client.deleteObject(new DeleteObjectRequest(bucketName, key));
    }
}