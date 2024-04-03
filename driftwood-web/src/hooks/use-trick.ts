export const useTrick = () => {
    const sleep = () => new Promise((resolve) => setTimeout(resolve, 1000));
    return {
        sleep,
    };
};
