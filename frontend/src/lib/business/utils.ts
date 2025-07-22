export function getFileSizeHr(size: number) {
  return size >= 1024 * 1024
    ? `${(size / (1024 * 1024)).toFixed(1)}MB`
    : size >= 1024
      ? `${(size / 1024).toFixed(1)}KB`
      : `${size}B`;
}

export function getDateHr(date: string) {
  return new Date(date).toLocaleDateString("ko-KR", {
    year: "2-digit",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  });
}
