import {MessageApiInjection} from "naive-ui/lib/message/src/MessageProvider";

export {};

declare global {
    interface Window {
        $message: MessageApiInjection;
    }
}
