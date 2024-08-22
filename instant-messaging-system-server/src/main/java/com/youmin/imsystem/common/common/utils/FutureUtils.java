package com.youmin.imsystem.common.common.utils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class FutureUtils {


    public static <T> CompletableFuture<List<T>> sequenceNonNull(Collection<CompletableFuture<T>> completableFutures){
        return CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[]{}))
                .thenApply(v->
                     completableFutures
                             .stream()
                             .map(CompletableFuture::join)
                             .filter(Objects::nonNull)
                             .collect(Collectors.toList())
                );
    }
}
