"use client";

import { useRouter } from "next/navigation";

import { components } from "@/lib/backend/apiV1/schema";

import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";

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
      <DialogContent>
        <DialogHeader>
          <DialogTitle>파일 미리보기</DialogTitle>
          <DialogDescription>{id}번 글의 파일(sample1.jpg)</DialogDescription>
        </DialogHeader>
        <div>파일번호 : {JSON.stringify(genFile)}</div>
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
