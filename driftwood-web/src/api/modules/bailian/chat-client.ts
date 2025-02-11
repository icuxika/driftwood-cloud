import AxiosInstance from "@/api/axios";
import { AxiosProgressEvent, AxiosResponse } from "axios";

type CreateService = (path: string) => {
    streamChat: (id: string, userInput?: string) => Promise<AxiosResponse>;
};
const createService: CreateService = (path: string) => {
    return {
        streamChat(id, userInput) {
            return AxiosInstance.get(`${path}/stream/chat/${id}`, {
                responseType: "stream",
                adapter: "fetch",
                timeout: 0,
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
