export const concatenateNames = (
  lastName: string,
  firstName?: string,
  middleInitial?: string,
  suffix?: string
): string => {
  let name = lastName;
  if (firstName) {
    name += `, ${firstName}`;
    if (middleInitial) {
      name += ` ${middleInitial}`;
    }
  }
  if (suffix) {
    name += `, ${suffix}`;
  }
  return name;
};
