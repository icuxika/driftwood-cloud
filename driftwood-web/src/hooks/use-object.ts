export const useObject = () => {
    const getPropertyValue = <T, K extends keyof T>(
        obj: T,
        key: string
    ): T[K] => obj[key as K];

    return {
        getPropertyValue,
    };
};
