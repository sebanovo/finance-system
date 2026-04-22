import { Injectable, signal } from "@angular/core";

export type CaptureKind = "photo" | "video" | "audio";

export type CaptureResult = {
  kind: CaptureKind;
  blob: Blob;
  url: string;
  filename: string;
  mimeType: string;
};

@Injectable({
  providedIn: "root",
})
export class MediaCaptureService {
  private previewStream: MediaStream | null = null;
  private audioStream: MediaStream | null = null;

  private videoRecorder: MediaRecorder | null = null;
  private audioRecorder: MediaRecorder | null = null;

  private videoChunks: Blob[] = [];
  private audioChunks: Blob[] = [];

  readonly previewStreamSignal = signal<MediaStream | null>(null);
  readonly isCameraOpen = signal(false);
  readonly isRecordingVideo = signal(false);
  readonly isRecordingAudio = signal(false);
  readonly photoResult = signal<CaptureResult | null>(null);
  readonly videoResult = signal<CaptureResult | null>(null);
  readonly audioResult = signal<CaptureResult | null>(null);
  readonly errorMessage = signal<string | null>(null);

  async openCamera(withAudio = true): Promise<MediaStream | null> {
    this.errorMessage.set(null);

    try {
      this.stopPreviewStream();

      const stream = await navigator.mediaDevices.getUserMedia({
        video: true,
        audio: withAudio,
      });

      this.previewStream = stream;
      this.previewStreamSignal.set(stream);
      this.isCameraOpen.set(true);

      return stream;
    } catch (error) {
      this.handleError(error, "No se pudo abrir la cámara");
      return null;
    }
  }

  async openMicrophoneOnly(): Promise<MediaStream | null> {
    this.errorMessage.set(null);

    try {
      this.stopAudioStream();

      const stream = await navigator.mediaDevices.getUserMedia({
        audio: true,
        video: false,
      });

      this.audioStream = stream;
      return stream;
    } catch (error) {
      this.handleError(error, "No se pudo acceder al micrófono");
      return null;
    }
  }

  async capturePhoto(
    videoElement: HTMLVideoElement
  ): Promise<CaptureResult | null> {
    this.errorMessage.set(null);

    try {
      if (!videoElement.videoWidth || !videoElement.videoHeight) {
        throw new Error("La cámara todavía no está lista para capturar");
      }

      const canvas = document.createElement("canvas");
      canvas.width = videoElement.videoWidth;
      canvas.height = videoElement.videoHeight;

      const context = canvas.getContext("2d");

      if (!context) {
        throw new Error("No se pudo obtener el contexto del canvas");
      }

      context.drawImage(videoElement, 0, 0, canvas.width, canvas.height);

      const blob = await this.canvasToBlob(canvas, "image/png");

      const result = this.buildResult("photo", blob, "captured-photo.png");

      this.revokeResultUrl(this.photoResult());
      this.photoResult.set(result);

      return result;
    } catch (error) {
      this.handleError(error, "No se pudo capturar la foto");
      return null;
    }
  }

  async startVideoRecording(): Promise<void> {
    this.errorMessage.set(null);

    try {
      if (!this.previewStream) {
        throw new Error("Primero debes abrir la cámara");
      }

      if (this.isRecordingVideo()) {
        return;
      }

      this.revokeResultUrl(this.videoResult());
      this.videoResult.set(null);

      this.videoChunks = [];

      const mimeType = this.getSupportedMimeType([
        "video/webm;codecs=vp9,opus",
        "video/webm;codecs=vp8,opus",
        "video/webm",
      ]);

      this.videoRecorder = mimeType
        ? new MediaRecorder(this.previewStream, { mimeType })
        : new MediaRecorder(this.previewStream);

      this.videoRecorder.ondataavailable = event => {
        if (event.data && event.data.size > 0) {
          this.videoChunks.push(event.data);
        }
      };

      this.videoRecorder.onerror = () => {
        this.errorMessage.set("Ocurrió un error durante la grabación de video");
      };

      this.videoRecorder.onstop = () => {
        const finalMimeType =
          this.videoRecorder?.mimeType || mimeType || "video/webm";

        const blob = new Blob(this.videoChunks, { type: finalMimeType });
        const result = this.buildResult("video", blob, "recorded-video.webm");

        this.videoResult.set(result);
        this.isRecordingVideo.set(false);
        this.videoChunks = [];
      };

      this.videoRecorder.start();
      this.isRecordingVideo.set(true);
    } catch (error) {
      this.handleError(error, "No se pudo iniciar la grabación de video");
    }
  }

  stopVideoRecording(): void {
    if (!this.videoRecorder || this.videoRecorder.state === "inactive") {
      return;
    }

    this.videoRecorder.stop();
  }

  async startAudioRecording(): Promise<void> {
    this.errorMessage.set(null);

    try {
      if (this.isRecordingAudio()) {
        return;
      }

      const stream = await this.openMicrophoneOnly();

      if (!stream) {
        return;
      }

      this.revokeResultUrl(this.audioResult());
      this.audioResult.set(null);

      this.audioChunks = [];

      const mimeType = this.getSupportedMimeType([
        "audio/webm;codecs=opus",
        "audio/webm",
      ]);

      this.audioRecorder = mimeType
        ? new MediaRecorder(stream, { mimeType })
        : new MediaRecorder(stream);

      this.audioRecorder.ondataavailable = event => {
        if (event.data && event.data.size > 0) {
          this.audioChunks.push(event.data);
        }
      };

      this.audioRecorder.onerror = () => {
        this.errorMessage.set("Ocurrió un error durante la grabación de audio");
      };

      this.audioRecorder.onstop = () => {
        const finalMimeType =
          this.audioRecorder?.mimeType || mimeType || "audio/webm";

        const blob = new Blob(this.audioChunks, { type: finalMimeType });
        const result = this.buildResult("audio", blob, "recorded-audio.webm");

        this.audioResult.set(result);
        this.isRecordingAudio.set(false);
        this.audioChunks = [];
        this.stopAudioStream();
      };

      this.audioRecorder.start();
      this.isRecordingAudio.set(true);
    } catch (error) {
      this.handleError(error, "No se pudo iniciar la grabación de audio");
    }
  }

  stopAudioRecording(): void {
    if (!this.audioRecorder || this.audioRecorder.state === "inactive") {
      return;
    }

    this.audioRecorder.stop();
  }

  closeAll(): void {
    this.stopVideoRecording();
    this.stopAudioRecording();
    this.stopPreviewStream();
    this.stopAudioStream();
    this.isCameraOpen.set(false);
  }

  downloadResult(result: CaptureResult | null): void {
    if (!result) {
      return;
    }

    const anchor = document.createElement("a");
    anchor.href = result.url;
    anchor.download = result.filename;
    anchor.click();
  }

  clearResults(): void {
    this.revokeResultUrl(this.photoResult());
    this.revokeResultUrl(this.videoResult());
    this.revokeResultUrl(this.audioResult());

    this.photoResult.set(null);
    this.videoResult.set(null);
    this.audioResult.set(null);
  }

  cleanup(): void {
    this.closeAll();
    this.clearResults();
    this.errorMessage.set(null);
  }

  private stopPreviewStream(): void {
    if (!this.previewStream) {
      return;
    }

    this.previewStream.getTracks().forEach(track => track.stop());
    this.previewStream = null;
    this.previewStreamSignal.set(null);
    this.isCameraOpen.set(false);
  }

  private stopAudioStream(): void {
    if (!this.audioStream) {
      return;
    }

    this.audioStream.getTracks().forEach(track => track.stop());
    this.audioStream = null;
  }

  private buildResult(
    kind: CaptureKind,
    blob: Blob,
    filename: string
  ): CaptureResult {
    const url = URL.createObjectURL(blob);

    return {
      kind,
      blob,
      url,
      filename,
      mimeType: blob.type,
    };
  }

  private revokeResultUrl(result: CaptureResult | null): void {
    if (!result?.url) {
      return;
    }

    URL.revokeObjectURL(result.url);
  }

  private canvasToBlob(
    canvas: HTMLCanvasElement,
    mimeType: string
  ): Promise<Blob> {
    return new Promise((resolve, reject) => {
      canvas.toBlob(blob => {
        if (!blob) {
          reject(new Error("No se pudo generar la imagen"));
          return;
        }

        resolve(blob);
      }, mimeType);
    });
  }

  private getSupportedMimeType(candidates: string[]): string | null {
    for (const candidate of candidates) {
      if (MediaRecorder.isTypeSupported(candidate)) {
        return candidate;
      }
    }

    return null;
  }

  private handleError(error: unknown, fallbackMessage: string): void {
    if (error instanceof DOMException) {
      this.errorMessage.set(`${fallbackMessage}: ${error.message}`);
      return;
    }

    if (error instanceof Error) {
      this.errorMessage.set(`${fallbackMessage}: ${error.message}`);
      return;
    }

    this.errorMessage.set(fallbackMessage);
  }
}
