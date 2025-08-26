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

export function getUploadableInputAccept() {
  return ".pdf,.doc,.docx,.xls,.xlsx,.ppt,.pptx,.txt,.csv,.zip,.rar,.7z,.jpg,.jpeg,.png,.gif,.webp,.svg,.mp4,.m4a,.mov,.mp3,.xml,.hwp,.hwpx,.md";
}

export function stripMarkdown(input: string) {
  // 1. $$...$$ 또는 ```...``` 내용을 제거
  const cleanedContext = input.replace(
    /(\$\$[\s\S]*?\$\$|```[\s\S]*?```)/g,
    "",
  );

  // 2. 마크다운 링크에서 텍스트만 추출 ([text](url) -> text)
  const withoutLinks = cleanedContext.replace(/\[([^\]]+)]\([^)]+\)/g, "$1");

  // 3. 영어, 소괄호, 한글(자음/모음 포함), 특수문자(:;/,<>=\-_[]), 띄어쓰기, 줄바꿈만 허용
  // 4. 연속된 공백과 줄바꿈을 하나의 공백으로 변경하고 앞뒤 공백 제거
  return withoutLinks
    .replace(/[^a-zA-Z가-힣ㄱ-하-ㅣ0-9().?!:;/,<>=\-_\[\]\s]/g, "")
    .replace(/\s+/g, " ")
    .trim()
    .slice(0, 157)
    .replace(/(.{157}).*/, "$1...");
}

export function getMetadataAttrValueFromQueryStr(
  url: string,
  attr: string,
  defaultValue: string,
) {
  const urlObj = new URL(url);
  const searchParams = new URLSearchParams(urlObj.search);
  return searchParams.get(attr) ?? defaultValue;
}

export function getMetadataAttrValueAsNumberFromQueryStr(
  url: string,
  attr: string,
  defaultValue: number,
) {
  const value = getMetadataAttrValueFromQueryStr(url, attr, "");
  return value.length > 0 ? parseInt(value) : defaultValue;
}

export function getSummaryFromContent(content: string) {
  let summary = content;

  if (summary.startsWith("# 요약")) {
    const startContent = summary.slice(4);
    const endIndex = startContent.search(/(\n\n|\#|\r\n)/);

    if (endIndex !== -1) {
      summary = startContent.slice(0, endIndex).trim();
    }

    //각 줄의 시작 부분에서 '- ' 또는 '-'를 제거
    summary = summary
      .split("\n")
      .map((line) => line.replace(/^-\s*/, ""))
      .join("\n");

    return summary.trim();
  }

  return "";
}

// 객체에서 특정 키만 필터링하는 유틸리티 함수
export function filterObjectKeys(
  obj: { [key: string]: any },
  allowedKeys: string[],
) {
  return Object.keys(obj).reduce((filtered: { [key: string]: any }, key) => {
    if (allowedKeys.includes(key)) {
      filtered[key] = obj[key];
    }
    return filtered;
  }, {});
}

export function isExternalUrl(url: string) {
  return url.startsWith("http") || url.startsWith("//");
}
