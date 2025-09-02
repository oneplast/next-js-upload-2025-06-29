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

function youtubePlugin() {
  const toHTMLRenderers = {
    youtube(node: any) {
      const html = renderYoutube(node.literal);
      return [
        { type: "openTag", tagName: "div", outerNewLine: true },
        { type: "html", content: html },
        { type: "closeTag", tagName: "div", outerNewLine: true },
      ];
    },
  };

  function renderYoutube(url: string) {
    url = url.replace("https://www.youtube.com/watch?v=", "");
    url = url.replace("http://www.youtube.com/watch?v=", "");
    url = url.replace("www.youtube.com/watch?v=", "");
    url = url.replace("youtube.com/watch?v=", "");
    url = url.replace("https://youtu.be/", "");
    url = url.replace("http://youtu.be/", "");
    url = url.replace("youtu.be/", "");

    const urlParams = getParamsFromUrl(url);

    const width = "100%";
    const height = "100%";
    const ratio = "aspect-[16/9]";
    let marginLeft = "auto";

    if (urlParams["margin-left"]) {
      marginLeft = urlParams["margin-left"];
    }

    let marginRight = "auto";

    if (urlParams["margin-right"]) {
      marginRight = urlParams["margin-right"];
    }

    let youtubeId = url;

    if (youtubeId.indexOf("?") !== -1) {
      const pos = url.indexOf("?");
      youtubeId = youtubeId.substring(0, pos);
    }

    return (
      '<div style="max-width:' +
      urlParams["max-width"] +
      "px; margin-left:" +
      marginLeft +
      "; margin-right:" +
      marginRight +
      ';" class="' +
      ratio +
      ' relative my-4"><iframe class="absolute top-0 left-0 w-full" width="' +
      width +
      '" height="' +
      height +
      '" src="https://www.youtube.com/embed/' +
      youtubeId +
      '?autoplay=1&mute=1&fs=1" ' +
      'allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; fullscreen" ' +
      'allowfullscreen="true"></iframe></div>'
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
          youtubePlugin,
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
