"use client";

import { use } from "react";
import { toast } from "sonner";

import Link from "next/link";
import { useRouter } from "next/navigation";

import client from "@/lib/backend/client";

import { LoginMemberContext } from "@/stores/auth/loginMember";

import { Button } from "@/components/ui/button";

import {
  MonitorCog,
  NotebookTabs,
  Pencil,
  TableOfContents,
  UserRoundSearch,
} from "lucide-react";

import LoginButton from "./LoginButton";
import Logo from "./Logo";
import MeMenuButton from "./MeMenuButton";
import ThemeToggleButton from "./ThemeToggleButton";

export default function WideHeaderContent({
  className,
}: {
  className?: string;
}) {
  const router = useRouter();
  const { isLogin, isUserPage, isAdminPage } = use(LoginMemberContext);

  return (
    <div className={`${className}  container mx-auto py-1`}>
      {isUserPage && (
        <>
          <Button variant="link" asChild>
            <Logo text />
          </Button>
          <Button variant="link" asChild>
            <Link href="/post/list">
              <TableOfContents />글
            </Link>
          </Button>
          {isLogin && (
            <Button
              variant="link"
              onClick={() =>
                client.POST("/api/v1/posts/temp").then((response) => {
                  if (response.error) {
                    toast(response.error.msg);
                    return;
                  } else {
                    toast(response.data.msg);

                    router.replace(`/post/${response.data.data.post.id}/edit`);
                  }
                })
              }
            >
              <Pencil /> 작성
            </Button>
          )}
          {isLogin && (
            <Button variant="link" asChild>
              <Link href="/post/mine">
                <NotebookTabs />
                내글
              </Link>
            </Button>
          )}
        </>
      )}

      {isAdminPage && (
        <>
          <Button variant="link" asChild>
            <Link href="/adm">
              <MonitorCog />
              관리자 홈
            </Link>
          </Button>

          <Button variant="link" asChild>
            <Link href="/adm/member/list">
              <UserRoundSearch />
              회원관리
            </Link>
          </Button>

          <Button variant="link" asChild>
            <Logo text />
          </Button>
        </>
      )}
      <div className="flex-grow"></div>
      {!isLogin && <LoginButton />}
      {isLogin && <MeMenuButton />}
      <ThemeToggleButton />
    </div>
  );
}
