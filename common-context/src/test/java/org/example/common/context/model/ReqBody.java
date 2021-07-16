package org.example.common.context.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.example.common.context.annotation.Header;

/**
 * @author 李红兵
 * @date 2021/5/28 14:24
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ReqBody {
    @Header("String")
    private String string;

    @Header("W-Integer")
    private Integer integer;

    @Header("P-int")
    private int anInt;

    @Header("W-Boolean")
    private Boolean booleanWrapper;

    @Header("P-boolean")
    private boolean aBoolean;
}
