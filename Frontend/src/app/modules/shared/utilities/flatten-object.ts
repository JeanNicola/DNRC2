export const flattenObject = (obj: unknown): { [key: string]: any } => {
  const flattened: { [key: string]: any } = {};

  Object.keys(obj).forEach((key) => {
    if (typeof obj[key] === 'object' && obj[key] !== null) {
      Object.assign(flattened, flattenObject(obj[key]));
    } else {
      flattened[key] = obj[key] as unknown;
    }
  });

  return flattened;
};
