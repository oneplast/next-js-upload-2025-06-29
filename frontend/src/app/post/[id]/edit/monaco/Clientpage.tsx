"use client";

import { useEffect, useRef } from "react";

import { components } from "@/lib/backend/apiV1/schema";

export default function ClientPage({
  post,
}: {
  post: components["schemas"]["PostWithContentDto"];
}) {
  const editorRef = useRef<HTMLDivElement>(null);
  const monacoRef = useRef<any>(null);

  useEffect(() => {
    if (typeof window === "undefined" || !editorRef.current) return;

    const initMonaco = () => {
      window.require.config({
        paths: {
          vs: "https://cdnjs.cloudflare.com/ajax/libs/monaco-editor/0.44.0/min/vs",
        },
      });

      window.require(["vs/editor/editor.main"], () => {
        monacoRef.current?.dispose();

        monacoRef.current = window.monaco.editor.create(editorRef.current!, {
          value: post.content,
          language: "markdown",
          theme: "vs-dark",
          tabSize: 2,
          mouseWheelZoom: true,
          automaticLayout: true,
          minimap: { enabled: false },
          fontSize: 14,
          wordWrap: "on",
          lineNumbers: "on",
        });
      });
    };

    if (window.require) {
      initMonaco();
    } else {
      const script = document.createElement("script");
      script.src =
        "https://cdnjs.cloudflare.com/ajax/libs/monaco-editor/0.44.0/min/vs/loader.min.js";
      script.onload = initMonaco;
      document.head.appendChild(script);
    }

    return () => monacoRef.current?.dispose();
  }, []);

  return (
    <div className="flex-1 flex">
      <div ref={editorRef} className="w-full" />
    </div>
  );
}
