"use client";

import { useRouter } from "next/navigation";

import { components } from "@/lib/backend/apiV1/schema";
import { getFileSizeHr } from "@/lib/business/utils";

import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";

import { Download } from "lucide-react";

export default function ClientPage({
  id,
  genFile,
}: {
  id: string;
  genFile: components["schemas"]["PostGenFileDto"];
}) {
  const router = useRouter();

  return (
    <Dialog
      open
      onOpenChange={() => {
        router.back();
      }}
    >
      <DialogContent className="max-w-[100dvh]">
        <DialogHeader>
          <DialogTitle>파일 미리보기</DialogTitle>
          <DialogDescription>
            {id}번 글의 파일({genFile.originalFileName})
          </DialogDescription>
        </DialogHeader>
        <div className="flex justify-center">
          <img src={genFile.publicUrl} alt="" />
        </div>
        <Button variant="link" asChild className="justify-start">
          <a href={genFile.downloadUrl} className="flex items-center gap-2">
            <Download />
            <span>
              {genFile.originalFileName}({getFileSizeHr(genFile.fileSize ?? 0)})
              다운로드
            </span>
          </a>
        </Button>
        <DialogFooter className="gap-2">
          <Button
            variant="outline"
            onClick={() => {
              router.back();
            }}
          >
            닫기
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
