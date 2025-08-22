"use client";

import { forwardRef } from "react";

import dynamic from "next/dynamic";

import { ToastUiEditorViewerCoreProps } from "./ToastUiEditorViewerCore";

const ToastUiEditorViewerCore = dynamic(
  () => import("./ToastUiEditorViewerCore"),
  {
    ssr: false,
    loading: () => <div>로딩중...</div>,
  },
);

type ViewerProps = ToastUiEditorViewerCoreProps;

const ToastUiEditorViewer = forwardRef<any, ViewerProps>((props, ref) => {
  return <ToastUiEditorViewerCore ref={ref} {...props} />;
});

ToastUiEditorViewer.displayName = "ToastUIEditorViewer";

export default ToastUiEditorViewer;
