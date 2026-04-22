import { CommonModule } from "@angular/common";
import {
  AfterViewInit,
  Component,
  DestroyRef,
  effect,
  ElementRef,
  inject,
  ViewChild,
} from "@angular/core";
import { MediaCaptureService } from "../../services/madia-capture.service";

@Component({
  selector: "app-media-lab-page",
  imports: [CommonModule],
  templateUrl: "./media-lab-page.html",
  styleUrl: "./media-lab-page.css",
})
export class MediaLabPage implements AfterViewInit {
  @ViewChild("previewVideo")
  private previewVideoRef?: ElementRef<HTMLVideoElement>;

  protected readonly media = inject(MediaCaptureService);
  private readonly destroyRef = inject(DestroyRef);

  constructor() {
    effect(() => {
      const stream = this.media.previewStreamSignal();
      const video = this.previewVideoRef?.nativeElement;

      if (!video) {
        return;
      }

      if (!stream) {
        video.srcObject = null;
        return;
      }

      video.srcObject = stream;
      void video.play().catch(() => {
        this.media.errorMessage.set("No se pudo reproducir la vista previa");
      });
    });

    this.destroyRef.onDestroy(() => {
      this.media.cleanup();
    });
  }

  ngAfterViewInit(): void {
    const stream = this.media.previewStreamSignal();
    const video = this.previewVideoRef?.nativeElement;

    if (stream && video) {
      video.srcObject = stream;
      void video.play().catch(() => {
        this.media.errorMessage.set("No se pudo reproducir la vista previa");
      });
    }
  }

  async openCamera(): Promise<void> {
    await this.media.openCamera(true);
  }

  async capturePhoto(): Promise<void> {
    const video = this.previewVideoRef?.nativeElement;

    if (!video) {
      this.media.errorMessage.set("No se encontró el elemento de video");
      return;
    }

    await this.media.capturePhoto(video);
  }

  async startVideoRecording(): Promise<void> {
    if (!this.media.isCameraOpen()) {
      await this.media.openCamera(true);
    }

    await this.media.startVideoRecording();
  }

  stopVideoRecording(): void {
    this.media.stopVideoRecording();
  }

  async startAudioRecording(): Promise<void> {
    await this.media.startAudioRecording();
  }

  stopAudioRecording(): void {
    this.media.stopAudioRecording();
  }

  closeAll(): void {
    this.media.closeAll();
  }

  clearResults(): void {
    this.media.clearResults();
  }

  downloadPhoto(): void {
    this.media.downloadResult(this.media.photoResult());
  }

  downloadVideo(): void {
    this.media.downloadResult(this.media.videoResult());
  }

  downloadAudio(): void {
    this.media.downloadResult(this.media.audioResult());
  }
}
