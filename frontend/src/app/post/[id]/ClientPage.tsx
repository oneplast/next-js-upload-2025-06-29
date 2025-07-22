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
  postGenFiles,
}: {
  post: components["schemas"]["PostWithContentDto"];
  postGenFiles: components["schemas"]["PostGenFileDto"][];
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
                className="rounded-full ring-2 ring-primary/10"
              />
              <div>
                <p className="text-sm font-medium text-foreground">
                  {post.authorName}
                </p>
                <p className="text-xs text-muted-foreground">
                  {new Date(post.createDate).toLocaleString("ko-KR", {
                    year: "2-digit",
                    month: "2-digit",
                    day: "2-digit",
                    hour: "2-digit",
                    minute: "2-digit",
                  })}
                </p>
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
              <p className="text-xs text-muted-foreground">
                {getDateHr(post.modifyDate)}에 수정됨
              </p>
            )}
          </div>
        </CardContent>
        <CardFooter>
          <div className="grid gap-4">
            {postGenFiles
              .filter((file) => file.typeCode === "attachment")
              .map((file) => (
                <div key={file.id} className="grid">
                  <Button variant="link" asChild className="justify-start">
                    <a
                      href={file.downloadUrl}
                      className="flex items-center gap-2"
                    >
                      <Download />
                      {file.fileExtTypeCode === "img" && (
                        <Image
                          src={file.publicUrl as string}
                          alt={file.originalFileName as string}
                          width={16}
                          height={16}
                          className="align-self h-[16px] w-[16px]"
                        />
                      )}
                      <span>
                        {file.originalFileName}(
                        {getFileSizeHr(file.fileSize ?? 0)}) 다운로드
                      </span>
                    </a>
                  </Button>
                  <Button variant="link" className="justify-start" asChild>
                    <Link href={`/post/${post.id}/genFilePreview`}>
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
