"use client";

// @ts-expect-error - 타입 정보 없음
import codeSyntaxHighlight from "@toast-ui/editor-plugin-code-syntax-highlight/dist/toastui-editor-plugin-code-syntax-highlight-all";
import "@toast-ui/editor/dist/i18n/ko-kr";
import "@toast-ui/editor/dist/theme/toastui-editor-dark.css";
import "@toast-ui/editor/dist/toastui-editor.css";
import { Viewer } from "@toast-ui/react-editor";
import { forwardRef } from "react";

import { filterObjectKeys } from "../utils";

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
        customHTMLRenderer={{
          htmlBlock: {
            iframe(node: any) {
              const newAttrs = filterObjectKeys(node.attrs, [
                "src",
                "width",
                "height",
                "allow",
                "allowfullscreen",
                "frameborder",
                "scrolling",
              ]);
              return [
                {
                  type: "openTag",
                  tagName: "iframe",
                  outerNewLine: true,
                  attributes: newAttrs,
                },
                {
                  type: "html",
                  content: node.childrenHTML,
                },
                {
                  type: "closeTag",
                  tagName: "iframe",
                  outerNewLine: false,
                },
              ];
            },
          },
        }}
      />
    );
  },
);

ToastUiEditorViewerCore.displayName = "ToastUIEditorViewerCore";

export default ToastUiEditorViewerCore;
