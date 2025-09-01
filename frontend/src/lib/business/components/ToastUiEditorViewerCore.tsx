"use client";

import chart from "@toast-ui/editor-plugin-chart";
// @ts-expect-error - 타입 정보 없음
import codeSyntaxHighlight from "@toast-ui/editor-plugin-code-syntax-highlight/dist/toastui-editor-plugin-code-syntax-highlight-all";
import tableMergedCellPlugin from "@toast-ui/editor-plugin-table-merged-cell";
import "@toast-ui/editor-plugin-table-merged-cell/dist/toastui-editor-plugin-table-merged-cell.css";
import umlPlugin from "@toast-ui/editor-plugin-uml";
import "@toast-ui/editor/dist/i18n/ko-kr";
import "@toast-ui/editor/dist/theme/toastui-editor-dark.css";
import "@toast-ui/editor/dist/toastui-editor.css";
import { Viewer } from "@toast-ui/react-editor";
import { forwardRef } from "react";

import { filterObjectKeys, getParamsFromUrl, isExternalUrl } from "../utils";

function hidePlugin() {
  const toHTMLRenderers = {
    hide(node: any) {
      return [
        { type: "openTag", tagName: "div", outerNewLine: true },
        { type: "html", content: "" },
        { type: "closeTag", tagName: "div", outerNewLine: true },
      ];
    },
  };

  return { toHTMLRenderers };
}

function pptPlugin() {
  const toHTMLRenderers = {
    ppt(node: any) {
      return [
        { type: "openTag", tagName: "div", outerNewLine: true },
        { type: "html", content: "" },
        { type: "closeTag", tagName: "div", outerNewLine: true },
      ];
    },
  };

  return { toHTMLRenderers };
}

function configPlugin() {
  const toHTMLRenderers = {
    config(node: any) {
      return [
        { type: "openTag", tagName: "div", outerNewLine: true },
        { type: "html", content: "" },
        { type: "closeTag", tagName: "div", outerNewLine: true },
      ];
    },
  };

  return { toHTMLRenderers };
}

function codepenPlugin() {
  const toHTMLRenderers = {
    codepen(node: any) {
      const html = renderCodepen(node.literal);
      return [
        { type: "openTag", tagName: "div", outerNawLine: true },
        { type: "html", content: html },
        { type: "closeTag", tagName: "div", outerNewLine: true },
      ];
    },
  };

  function renderCodepen(url: string) {
    const urlParams = getParamsFromUrl(url);

    let height = "400";

    if (urlParams.height) {
      height = urlParams.height;
    }

    let width = "100%";

    if (urlParams.width) {
      width = urlParams.width;
    }

    if (!width.includes("px") && !width.includes("%")) {
      width += "px";
    }

    let iframeUri = url;

    if (iframeUri.indexOf("#") !== -1) {
      const pos = iframeUri.indexOf("#");
      iframeUri = iframeUri.substring(0, pos);
    }

    return (
      '<iframe class="my-4" height="' +
      height +
      '" style="width: ' +
      width +
      ';" title="" src="' +
      iframeUri +
      '" allowtransparency="true" allowfullscreen="true"></iframe>'
    );
  }

  return { toHTMLRenderers };
}

export interface ToastUiEditorViewerCoreProps {
  initialValue: string;
  theme: "dark" | "light";
}
const ToastUiEditorViewerCore = forwardRef<any, ToastUiEditorViewerCoreProps>(
  (props, ref) => {
    return (
      <Viewer
        theme={props.theme}
        plugins={[
          codepenPlugin,
          hidePlugin,
          pptPlugin,
          configPlugin,
          codeSyntaxHighlight,
          [
            chart,
            {
              minWidth: 100,
              maxWidth: 800,
              minHeight: 100,
              maxHeight: 400,
            },
          ],
          tableMergedCellPlugin,
          [
            umlPlugin,
            {
              rendererURL: "https://www.plantuml.com/plantuml/svg/",
            },
          ],
        ]}
        ref={ref}
        initialValue={props.initialValue}
        language="ko-KR"
        customHTMLRenderer={{
          heading(node: any, { entering, getChildrenText }: any) {
            return {
              type: entering ? "openTag" : "closeTag",
              tagName: `h${node.level}`,
              attributes: {
                id: getChildrenText(node).trim().replaceAll(" ", "-"),
              },
            };
          },
          link(node: any, { entering }: any) {
            return {
              type: entering ? "openTag" : "closeTag",
              tagName: `a`,
              attributes: {
                href: node.destination,
                target: isExternalUrl(node.destination) ? "_blank" : "_self",
              },
            };
          },
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
                "class",
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
