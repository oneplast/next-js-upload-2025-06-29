"use client";

import { use } from "react";

import ClientPage from "./ClientPage";

export default function Page({
  params,
}: {
  params: Promise<{ id: string; fileId: string }>;
}) {
  const { id, fileId } = use(params);

  return <ClientPage id={id} fileId={fileId} />;
}
