import dayjs from "dayjs";
import timezone from "dayjs/plugin/timezone";
import utc from "dayjs/plugin/utc";

dayjs.extend(utc);
dayjs.extend(timezone);


export function formatDatetime(
  date: string | Date | undefined | null,
  tz?: string
): string {
  if (!date) {
    return "";
  }
  return dayjs(date).tz(tz || dayjs.tz.guess()).format("YYYY-MM-DD HH:mm");
}
