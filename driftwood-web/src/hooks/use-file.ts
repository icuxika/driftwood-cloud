import { AxiosResponse } from "axios";

export const useFile = () => {
	const getFilename = (contentDisposition: string) => {
		if (
			contentDisposition.includes("filename") ||
			contentDisposition.includes("filename*")
		) {
			if (contentDisposition.includes("filename*")) {
				return decodeURIComponent(
					contentDisposition.split("filename*=")[1].split("''")[1]
				);
			} else {
				return decodeURIComponent(
					// eslint-disable-next-line prettier/prettier
          contentDisposition.split("filename=")[1].replaceAll("\"", "")
				);
			}
		} else {
			return "undefined";
		}
	};

	const downloadFile = (response: AxiosResponse) => {
		const url = window.URL.createObjectURL(new Blob([response.data]));
		const link = document.createElement("a");
		link.href = url;
		link.download = getFilename(response.headers["content-disposition"]);
		document.body.appendChild(link);
		link.click();
		document.body.removeChild(link);
		URL.revokeObjectURL(url);
	};

	return {
		downloadFile,
	};
};
