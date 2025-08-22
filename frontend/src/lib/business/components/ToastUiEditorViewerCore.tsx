"use client";

// @ts-expect-error - 타입 정보 없음
import codeSyntaxHighlight from "@toast-ui/editor-plugin-code-syntax-highlight/dist/toastui-editor-plugin-code-syntax-highlight-all";
import "@toast-ui/editor/dist/i18n/ko-kr";
import "@toast-ui/editor/dist/theme/toastui-editor-dark.css";
import "@toast-ui/editor/dist/toastui-editor.css";
import { Viewer } from "@toast-ui/react-editor";
import "prismjs/themes/prism.css";
import { forwardRef } from "react";

export interface ToastUiEditorViewerCoreProps {
  initialValue: string;
  theme: "dark" | "light";
}
const ToastUiEditorViewerCore = forwardRef<any, ToastUiEditorViewerCoreProps>(
  (props, ref) => {
    return (
      <Viewer
        theme={props.theme}
        plugins={[codeSyntaxHighlight]}
        ref={ref}
        initialValue={props.initialValue}
        language="ko-KR"
      />
    );
  },
);

ToastUiEditorViewerCore.displayName = "ToastUIEditorViewerCore";

export default ToastUiEditorViewerCore;
