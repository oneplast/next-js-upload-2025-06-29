"use client";

import "@toast-ui/editor/dist/i18n/ko-kr";
import "@toast-ui/editor/dist/toastui-editor.css";
import { Viewer } from "@toast-ui/react-editor";
import { forwardRef } from "react";

interface ToastUiEditorViewerCoreProps {
  initialValue: string;
}

const ToastUiEditorViewerCore = forwardRef<any, ToastUiEditorViewerCoreProps>(
  (props, ref) => {
    return (
      <Viewer ref={ref} initialValue={props.initialValue} language="ko-KR" />
    );
  },
);

ToastUiEditorViewerCore.displayName = "ToastUIEditorViewerCore";

export default ToastUiEditorViewerCore;
