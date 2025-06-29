import {
  PaginationResult,
  calculateNextEllipsisNumber,
  calculatePrevEllipsisNumber,
  createPageButtonUrl,
  generateMiddlePages,
} from "./PaginationType1";

interface UsePaginationProps {
  baseQueryString: string;
  totalPages: number;
  currentPageNumber: number;
  paginationArmSize: number;
}

export function usePagination({
  baseQueryString,
  totalPages,
  currentPageNumber,
  paginationArmSize,
}: UsePaginationProps): PaginationResult {
  const pageButtonUrl = createPageButtonUrl(baseQueryString);

  const prevEllipsisButtonPageNumber = calculatePrevEllipsisNumber(
    currentPageNumber,
    paginationArmSize,
  );

  const nextEllipsisButtonPageNumber = calculateNextEllipsisNumber(
    currentPageNumber,
    paginationArmSize,
    totalPages,
  );

  const middlePages = generateMiddlePages(
    totalPages,
    currentPageNumber,
    paginationArmSize,
    prevEllipsisButtonPageNumber,
    nextEllipsisButtonPageNumber,
  );

  return {
    pageButtonUrl,
    prevEllipsisButtonPageNumber,
    nextEllipsisButtonPageNumber,
    middlePages,
  };
}
