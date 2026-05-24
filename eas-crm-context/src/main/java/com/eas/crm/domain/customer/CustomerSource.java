package com.eas.crm.domain.customer;

public enum CustomerSource {
    REFERRAL("推荐", "客户推荐"),
    EXHIBITION("展会", "展会获取"),
    WEBSITE("网站", "网站咨询"),
    COLD_CALL("电销", "电话销售"),
    OTHER("其他", "其他渠道");

    private final String code;
    private final String description;

    CustomerSource(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
