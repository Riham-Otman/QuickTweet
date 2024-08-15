import { Dispatch, SetStateAction } from "react";
import { Toast, ToastContainer } from "react-bootstrap";

/**
 * Displays a toast notification for errors. It is a controlled component that requires `show` and `setShow` props to control its visibility.
 *
 * @component
 * @param {Object} props - The component props.
 * @param {string} props.title - The title of the error toast.
 * @param {string} props.msg - The message to be displayed in the toast.
 * @param {boolean} props.show - Controls the visibility of the toast. When `true`, the toast is shown.
 * @param {Dispatch<SetStateAction<boolean>>} props.setShow - A function to update the `show` state, typically to hide the toast.
 * @returns {React.ReactElement} A ToastContainer component containing a Toast component.
 */
const ErrorToast = ({
  title,
  msg,
  show,
  setShow,
}: {
  title: string;
  msg: string;
  show: boolean;
  setShow: Dispatch<SetStateAction<boolean>>;
}): React.ReactElement => {
  return (
    <ToastContainer position='top-end' className='p-3' style={{ zIndex: 1 }}>
      <Toast autohide={true} onClose={() => setShow(false)} show={show}>
        <Toast.Header>
          <strong className='me-auto'>{title}</strong>
        </Toast.Header>
        <Toast.Body>{msg}</Toast.Body>
      </Toast>
    </ToastContainer>
  );
};

export default ErrorToast;
