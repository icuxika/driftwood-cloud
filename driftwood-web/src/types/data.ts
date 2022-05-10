import { DropdownMixedOption } from "naive-ui/es/dropdown/src/interface";

const OPTION_CREATE_NAME = "新增";
export const OPTION_CREATE_KEY = "CREATE";
const OPTION_READ_NAME = "查询";
export const OPTION_READ_KEY = "READ";
const OPTION_UPDATE_NAME = "更新";
export const OPTION_UPDATE_KEY = "UPDATE";
const OPTION_DELETE_NAME = "删除";
export const OPTION_DELETE_KEY = "DELETE";

export const C = 0b0001;
export const R = 0b0010;
export const U = 0b0100;
export const D = 0b1000;

export const defineDropdownCRUDOptions = (
	operateMixed: number,
	additionalOptions?: DropdownMixedOption[]
): DropdownMixedOption[] => {
	const options: DropdownMixedOption[] = [];
	if (operateMixed & C) {
		options.push({
			label: OPTION_CREATE_NAME,
			key: OPTION_CREATE_KEY,
		});
	}
	if (operateMixed & R) {
		options.push({
			label: OPTION_READ_NAME,
			key: OPTION_READ_KEY,
		});
	}
	if (operateMixed & U) {
		options.push({
			label: OPTION_UPDATE_NAME,
			key: OPTION_UPDATE_KEY,
		});
	}
	if (operateMixed & D) {
		options.push({
			label: OPTION_DELETE_NAME,
			key: OPTION_DELETE_KEY,
		});
	}
	if (additionalOptions) {
		options.push(...additionalOptions);
	}
	return options;
};
