"use client";

import { use } from "react";

import Image from "next/image";
import Link from "next/link";

import { components } from "@/lib/backend/apiV1/schema";
import { getDateHr, getFileSizeHr } from "@/lib/business/utils";

import { LoginMemberContext } from "@/stores/auth/loginMember";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";

import { Download, Eye, ListX, Lock } from "lucide-react";

export default function ClientPage({
  post,
  genFiles,
}: {
  post: components["schemas"]["PostWithContentDto"];
  genFiles: components["schemas"]["PostGenFileDto"][];
}) {
  const { loginMember, isAdmin } = use(LoginMemberContext);

  return (
    <main className="container mt-2 mx-auto px-2">
      <Card>
        <CardHeader>
          <CardTitle className="mb-4 flex items-center gap-2">
            <Badge variant="outline">{post.id}</Badge>
            <div>{post.title}</div>
            {!post.published && <Lock className="w-4 h-4 flex-shrink-0" />}
            {!post.listed && <ListX className="w-4 h-4 flex-shrink-0" />}
          </CardTitle>
          <CardDescription className="sr-only">
            게시물 상세 내용
          </CardDescription>
          <div className="flex items-center gap-4 flex-wrap">
            <div className="flex items-center gap-4 w-full sm:w-auto">
              <Image
                src={post.authorProfileImgUrl}
                alt={post.authorName}
                width={40}
                height={40}
                quality={100}
                className="w-[40px] h-[40px] object-cover rounded-full ring-2 ring-primary/10"
              />
              <div>
                <div className="text-sm font-medium text-foreground">
                  {post.authorName}
                </div>
                <div className="text-sm text-muted-foreground">
                  {new Date(post.createDate).toLocaleString("ko-KR", {
                    year: "2-digit",
                    month: "2-digit",
                    day: "2-digit",
                    hour: "2-digit",
                    minute: "2-digit",
                  })}
                </div>
              </div>
            </div>
            <div className="flex-grow"></div>
            <div className="flex items-center gap-2">
              {loginMember.id === post.authorId && (
                <Button asChild variant="outline">
                  <Link href={`/post/${post.id}/edit`}>수정</Link>
                </Button>
              )}
              {(isAdmin || loginMember.id === post.authorId) && (
                <Button asChild variant="outline">
                  <Link href={`/post/${post.id}/delete`}>삭제</Link>
                </Button>
              )}
            </div>
          </div>
        </CardHeader>
        <CardContent>
          <div className="whitespace-pre-line">{post.content}</div>
          <div>
            {post.createDate != post.modifyDate && (
              <div className="mt-4 text-sm text-muted-foreground">
                {getDateHr(post.modifyDate)}에 수정됨
              </div>
            )}
          </div>
        </CardContent>
        <CardFooter>
          <div className="grid gap-4">
            {genFiles
              .filter((genFile) => genFile.typeCode === "attachment")
              .map((genFile) => (
                <div key={genFile.id} className="grid">
                  <Button variant="link" asChild className="justify-start">
                    <a
                      href={genFile.downloadUrl}
                      className="flex items-center gap-2"
                    >
                      <Download />
                      <span>
                        {genFile.originalFileName}
                        <br />({getFileSizeHr(genFile.fileSize ?? 0)}) 다운로드
                      </span>
                    </a>
                  </Button>
                  <Button variant="link" className="justify-start" asChild>
                    <Link
                      href={`/post/${post.id}/genFile/${genFile.id}/preview`}
                    >
                      <Eye />
                      <span>미리보기</span>
                    </Link>
                  </Button>
                </div>
              ))}
          </div>
        </CardFooter>
      </Card>
    </main>
  );
}
