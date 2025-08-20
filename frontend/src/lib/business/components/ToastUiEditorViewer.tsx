"use client";

import { forwardRef } from "react";

import dynamic from "next/dynamic";

const ToastUiEditorViewerCore = dynamic(
  () => import("./ToastUiEditorViewerCore"),
  {
    ssr: false,
    loading: () => <div>로딩중...</div>,
  },
);

interface ViewerProps {
  initialValue: string;
}

const ToastUiEditorViewer = forwardRef<any, ViewerProps>((props, ref) => {
  return <ToastUiEditorViewerCore ref={ref} {...props} />;
});

ToastUiEditorViewer.displayName = "ToastUIEditorViewer";

export default ToastUiEditorViewer;
