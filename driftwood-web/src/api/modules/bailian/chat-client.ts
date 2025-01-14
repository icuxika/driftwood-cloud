import AxiosInstance from "@/api/axios";
import { AxiosProgressEvent, AxiosResponse } from "axios";

type CreateService = (path: string) => {
    streamChat: (userInput?: string) => Promise<AxiosResponse>;
};
const createService: CreateService = (path: string) => {
    return {
        streamChat(userInput) {
            return AxiosInstance.get(`${path}/stream/chat`, {
                responseType: "stream",
                adapter: "fetch",
                timeout: 1000 * 50,
                onDownloadProgress: function (
                    progressEvent: AxiosProgressEvent
                ) {},
                params: {
                    userInput,
                },
            });
        },
    };
};

const chatClientService = createService("/bailian/chat-client");

export { chatClientService };
