package com.youmin.oss;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.youmin.oss.domain.OssReq;
import com.youmin.oss.domain.OssResp;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.util.Date;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MinIoTemplate {

    private MinioClient minioClient;

    private OssProperties ossProperties;

    @SneakyThrows
    public OssResp getPresignedUrl(OssReq ossReq){
        String absolutePath = ossReq.isAutoPath()?generatePath(ossReq):ossReq.getFilePath()+ StrUtil.SLASH+ossReq.getFilename();
        String url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .method(Method.PUT)
                .bucket(ossProperties.getBucketName())
                .object(absolutePath)
                .expiry(60 * 60 * 24)
                .build());
        return OssResp.builder()
                .uploadUrl(url)
                .downloadUrl(getDownloadUrl(ossProperties.getBucketName(), absolutePath))
                .build();



    }

    public String getDownloadUrl(String bucket,String pathFile ){
        return ossProperties.getEndpoint()+StrUtil.SLASH+bucket+pathFile;
    }

    private String generatePath(OssReq ossReq) {
        String uid = Optional.ofNullable(ossReq.getUid()).map(String::valueOf).orElse("0000000");
        UUID uuid = UUID.fastUUID();
        String suffix = FileNameUtil.getSuffix(ossReq.getFilename());
        String yearAndMonth = DateUtil.format(new Date(), DatePattern.NORM_MONTH_PATTERN);
        return ossReq.getFilePath()+StrUtil.SLASH+yearAndMonth+StrUtil.SLASH+uid+StrUtil.SLASH+uuid+StrUtil.DOT+suffix;
    }

}
