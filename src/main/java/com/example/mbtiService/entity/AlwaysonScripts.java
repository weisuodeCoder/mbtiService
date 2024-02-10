package com.example.mbtiService.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 参考：https://zhuanlan.zhihu.com/p/624042359
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlwaysonScripts {
    private ADetailer aDetailer;
}