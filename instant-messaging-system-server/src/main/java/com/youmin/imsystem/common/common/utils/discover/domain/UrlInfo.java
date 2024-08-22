package com.youmin.imsystem.common.common.utils.discover.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UrlInfo {

    /**
     * website title
     */
    private String title;

    /**
     * website image
     */
    private String image;

    /**
     * website description
     */
    private String description;
}
