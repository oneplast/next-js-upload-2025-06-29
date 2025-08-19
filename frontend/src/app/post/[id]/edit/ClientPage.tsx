"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import html2canvas from "html2canvas";
import { useForm } from "react-hook-form";
import { toast } from "sonner";
import { z } from "zod";

import Link from "next/link";
import { useRouter } from "next/navigation";

import client from "@/lib/backend/client";

import { components } from "@/lib/backend/apiV1/schema";
import {
  getSummaryFromContent,
  getUploadableInputAccept,
} from "@/lib/business/utils";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Checkbox } from "@/components/ui/checkbox";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";

const wrtieFormSchema = z.object({
  title: z
    .string()
    .trim()
    .min(1, "제목을 입력해주세요.")
    .min(2, "제목은 2자 이상이어야 합니다.")
    .max(50, "제목은 50자 이하여야 합니다."),
  content: z
    .string()
    .trim()
    .min(1, "내용을 입력해주세요.")
    .min(2, "내용은 2자 이상이어야 합니다.")
    .max(10_000_000, "내용은 1,000만자 이하여야 합니다."),
  published: z.boolean().optional(),
  listed: z.boolean().optional(),
  attachment_0: z.array(z.instanceof(File)).optional(),
});

type WriteFormInputs = z.infer<typeof wrtieFormSchema>;

export default function ClientPage({
  post,
}: {
  post: components["schemas"]["PostWithContentDto"];
}) {
  const router = useRouter();

  const form = useForm<WriteFormInputs>({
    resolver: zodResolver(wrtieFormSchema),
    defaultValues: {
      title: post.title,
      content: post.content,
      published: post.published,
      listed: post.listed,
    },
  });

  const handleThumbnailUpload = async (content: string, postId: number) => {
    const tempDiv = document.createElement("div");

    Object.assign(tempDiv.style, {
      width: "1200px",
      height: "1200px",
      position: "absolute",
      left: "-9999px",
      whiteSpace: "pre-wrap",
      wordBreak: "break-word",
      fontSize: "60px",
      fontWeight: "500",
      padding: "0 10px",
      display: "flex",
      flexWrap: "wrap",
      alignItems: "center",
      justifyContent: "center",
      color: "black",
    });

    const summary = getSummaryFromContent(content);

    tempDiv.innerText = summary || content;
    document.body.appendChild(tempDiv);

    try {
      const canvas = await html2canvas(tempDiv, {
        width: 1200,
        height: 1200,
      });

      const blob = await new Promise<Blob>((resolve) => {
        canvas.toBlob((blob) => resolve(blob!), "image/png", 1.0);
      });

      const file = new File([blob], `${postId}-thumbnail.png`, {
        type: "image/png",
        lastModified: Date.now(),
      });

      const formData = new FormData();

      formData.append("file", file);
      formData.append("metaStr", "darkInvertible=1");

      return await client.PUT(
        "/api/v1/posts/{postId}/genFiles/{typeCode}/{fileNo}",
        {
          params: {
            path: {
              postId,
              typeCode: "thumbnail",
              fileNo: 1,
            },
          },
          body: formData as any,
        },
      );
    } finally {
      document.body.removeChild(tempDiv);
    }
  };

  const handleAttachmentUpload = async (files: File[], postId: number) => {
    const formData = new FormData();
    files.forEach((file) => formData.append("files", file));

    return await client.POST("/api/v1/posts/{postId}/genFiles/{typeCode}", {
      params: {
        path: {
          postId,
          typeCode: "attachment",
        },
      },
      body: formData as any,
    });
  };

  const onSubmit = async (data: WriteFormInputs) => {
    // 데이터가 변경되었는지 확인
    const isPostDataChanged =
      data.title !== post.title ||
      data.content !== post.content ||
      data.published !== post.published ||
      data.listed !== post.listed;

    const isPostContentChanged = data.content !== post.content;

    if (isPostDataChanged) {
      const response = await client.PUT("/api/v1/posts/{id}", {
        params: {
          path: {
            id: post.id,
          },
        },
        body: {
          title: data.title,
          content: data.content,
          published: data.published,
          listed: data.listed,
        },
      });

      if (response.error) {
        toast(response.error.msg);
        return;
      }

      toast(response.data.msg);

      if (isPostContentChanged) {
        const thumbnailResponse = await handleThumbnailUpload(
          data.content,
          post.id,
        );

        if (thumbnailResponse.error) {
          toast(thumbnailResponse.error.msg);
        }
      }
    }

    if (data.attachment_0) {
      const uploadResponse = await handleAttachmentUpload(
        data.attachment_0,
        post.id,
      );

      if (uploadResponse.error) {
        toast(uploadResponse.error.msg);
        return;
      }

      toast(uploadResponse.data.msg);
    }

    router.replace("/post/list");
  };

  return (
    <div className="container mx-auto px-4">
      <h1 className="text-2xl font-bold my-4 flex items-center gap-2 justify-center">
        {post.id}번 글 수정
      </h1>

      <Form {...form}>
        <form
          onSubmit={form.handleSubmit(onSubmit)}
          className="flex flex-col gap-4"
        >
          <FormField
            control={form.control}
            name="title"
            render={({ field }) => (
              <FormItem>
                <FormLabel>제목</FormLabel>
                <FormControl>
                  <Input
                    {...field}
                    type="text"
                    placeholder={post.title}
                    autoComplete="off"
                    autoFocus
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          <div className="flex gap-4">
            <label className="flex items-center space-x-2 cursor-pointer">
              <Checkbox
                checked={form.watch("published")}
                onCheckedChange={(checked) =>
                  form.setValue("published", checked === true)
                }
              />
              <span className="text-sm font-medium leading-none">공개</span>
            </label>
            <label className="flex items-center space-x-2 cursor-pointer">
              <Checkbox
                checked={form.watch("listed")}
                onCheckedChange={(checked) =>
                  form.setValue("listed", checked === true)
                }
              />
              <span className="text-sm font-medium leading-none">검색</span>
            </label>
            <Badge variant="outline">작성자 : {post.authorName}</Badge>
          </div>
          <FormField
            control={form.control}
            name="attachment_0"
            render={({ field: { onChange, ...field } }) => (
              <FormItem>
                <FormLabel>첨부파일(드래그 앤 드롭 가능, 최대 5개)</FormLabel>
                <FormControl>
                  <Input
                    type="file"
                    multiple
                    accept={getUploadableInputAccept()}
                    onChange={(e) => {
                      const files = Array.from(e.target.files || []);
                      onChange(files);
                    }}
                    {...field}
                    value={undefined}
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          <Button variant="outline" asChild>
            <Link href={`/post/${post.id}/genFile/listForEdit`}>
              기존 첨부파일 변경/삭제
            </Link>
          </Button>
          <FormField
            control={form.control}
            name="content"
            render={({ field }) => (
              <FormItem>
                <FormLabel>내용</FormLabel>
                <FormControl>
                  <Textarea
                    {...field}
                    autoFocus
                    className="h-[calc(100dvh-520px)] min-h-[300px]"
                    placeholder={post.content}
                  />
                </FormControl>
              </FormItem>
            )}
          />
          <Button
            type="submit"
            disabled={form.formState.isSubmitting}
            className="mt-2"
          >
            {form.formState.isSubmitting ? "수정 중..." : "수정"}
          </Button>
        </form>
      </Form>
    </div>
  );
}
