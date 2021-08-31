package com.i4biz.mygarden.domain.catalog.options;

import lombok.Data;

import java.io.Serializable;
import java.sql.Date;

@Data
public class ItemOptions implements Serializable {
    Date floweringFrom;
    Date floweringTo;
}
