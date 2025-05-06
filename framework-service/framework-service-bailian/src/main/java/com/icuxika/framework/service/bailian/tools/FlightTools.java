package com.icuxika.framework.service.bailian.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class FlightTools {

    @Tool(description = "退订航班")
    CancelFlightBookingResponse cancelFlightBooking(CancelFlightBookingRequest cancelFlightBookingRequest) {
        System.out.println("航班序号: " + cancelFlightBookingRequest.number());
        System.out.println("客户名称: " + cancelFlightBookingRequest.name());
        if (cancelFlightBookingRequest.number().startsWith("no")) {
            return new CancelFlightBookingResponse("FAILURE", "退订失败");
        }
        return new CancelFlightBookingResponse("SUCCESS", "退订成功");
    }
}

record CancelFlightBookingRequest(@ToolParam(description = "航班的序号") String number,
                                  @ToolParam(description = "客户的名称") String name) {
}

record CancelFlightBookingResponse(@ToolParam(description = "状态") String status,
                                   @ToolParam(description = "信息") String message) {
}
