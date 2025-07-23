"use client";

import { use } from "react";

import ClientPage from "./ClientPage";

export default function Page({ params }: { params: Promise<{ id: string }> }) {
  const { id } = use(params);

  return <ClientPage id={id} />;
}
